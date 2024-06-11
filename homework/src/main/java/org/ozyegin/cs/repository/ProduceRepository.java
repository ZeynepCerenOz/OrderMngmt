package org.ozyegin.cs.repository;

import javax.sql.DataSource;

import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Product;
import org.ozyegin.cs.entity.Production;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {
  final int batchSize = 10;

  final String createPS = "INSERT INTO produce (company_name, product_id, capacity) VALUES(?,?,?)";
    final String getAllPS = "SELECT * FROM produce";
    final String deletePS = "DELETE FROM produce WHERE produce_id=?";
    final String deleteAllPS = "DELETE FROM produce";


  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

    private final RowMapper<Production> produceRowMapper = (resultSet, i) -> new Production()
            .productionId(resultSet.getInt("produce_id"))
            .company(resultSet.getString("company_name"))
            .productId(resultSet.getInt("product_id"))
            .capacity(resultSet.getInt("capacity"));

  public Integer produce(String company, int product, int capacity) {

    List<Production> produces = new ArrayList<>();
   Production produce=new Production();
   produce.setCompany(company);
   produce.setProductId(product);
   produce.setCapacity(capacity);
   produces.add(produce);
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, produces,
            batchSize,
            (ps, produce1) -> {
              ps.setObject(1, produce1.getCompany(), Types.VARCHAR); // Assuming product_id is an INTEGER
              ps.setObject(2, produce1.getProductId(), Types.INTEGER);
              ps.setObject(3, produce1.getCapacity(), Types.INTEGER);
            });


    List<Production> pList=getAll();

    return pList.get(0).getProductionId();
  }
    public List<Production> getAll() {
        return Objects.requireNonNull(getJdbcTemplate()).query(getAllPS, produceRowMapper);
    }

   public void delete(int produceId) throws Exception {

      if (Objects.requireNonNull(getJdbcTemplate()).update(deletePS,
              produceId) != 1) {
          throw new Exception("Produce Delete is failed!");
      }

  }

  public void deleteAll() {

      Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);

  }
}
