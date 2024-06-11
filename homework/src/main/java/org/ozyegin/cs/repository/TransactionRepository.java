package org.ozyegin.cs.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

import org.ozyegin.cs.entity.Product;
import org.ozyegin.cs.entity.Production;
import org.ozyegin.cs.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends JdbcDaoSupport {

  final int batchSize = 10;

  final String createPS = "INSERT INTO transaction (company_name, product_id, amount, created_date) VALUES(?,?,?,?)";

  final String getAllPS = "SELECT * FROM transaction";
  final String deletePS = "INSERT INTO transaction_history (transaction_id, company_name, product_id, amount, created_date) " +
          "SELECT transaction_id, company_name, product_id, amount, created_date FROM transaction WHERE transaction_id=?" +
          "; DELETE FROM transaction WHERE transaction_id=?";
  final String deleteAllPS = "DELETE FROM transaction";

  final String getSinglePS = "SELECT * FROM transaction WHERE transaction=?";

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  @Autowired
  private ProduceRepository produceRepository;

  private final RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> new Transaction()
          .transactionId(resultSet.getInt("transaction_id"))
          .company(resultSet.getString("company_name"))
          .productId(resultSet.getInt("product_id"))
          .amount(resultSet.getInt("amount"))
          .createdDate(resultSet.getDate("created_date"));

  public Integer order(String company, int product, int amount, Date createdDate) {

    List<Transaction> transactions = new ArrayList<>();
    Transaction transaction=new Transaction();
    transaction.setCompany(company);
    transaction.setProductId(product);
    transaction.setAmount(amount);
    transaction.setCreatedDate(createdDate);
    transactions.add(transaction);
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, transactions,
            batchSize,
            (ps, transaction1) -> {
              ps.setObject(1, transaction1.getCompany(), Types.VARCHAR); // Assuming product_id is an INTEGER
              ps.setObject(2, transaction1.getProductId(), Types.INTEGER);
              ps.setObject(3, transaction1.getAmount(), Types.INTEGER);
              ps.setObject(4, transaction1.getCreatedDate(), Types.DATE);
            });


    List<Transaction> tList=getAll();

    produceRepository.produce(company, product, amount*(-1));

    return tList.get(0).getTransactionId();
  }
  public List<Transaction> getAll() {
    return Objects.requireNonNull(getJdbcTemplate()).query(getAllPS, transactionRowMapper);
  }

  public Transaction find(int id) {
    try {
      return Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSinglePS,
              new Object[] {id},
              transactionRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return null; // Return null indicating product not found
    }
  }

  public void delete(int transactionId) throws Exception {


    if (Objects.requireNonNull(getJdbcTemplate()).update(deletePS,
            transactionId, transactionId) != 1) {
      throw new Exception("Transaction Delete is failed!");
    }
  }

  public void deleteAll() {

    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }
}
