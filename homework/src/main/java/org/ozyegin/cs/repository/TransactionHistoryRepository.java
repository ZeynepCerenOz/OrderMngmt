package org.ozyegin.cs.repository;

import java.sql.Date;
import java.util.*;
import javax.sql.DataSource;

import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Email;
import org.ozyegin.cs.entity.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionHistoryRepository extends JdbcDaoSupport {

  final String getMaxProductByCompanyPS = "  WITH total_orders AS (\n" +
          "        SELECT total.company_name, total.product_id, SUM(total.amount) AS total_amount\n" +
          "        FROM (\n" +
          "            SELECT th.amount, th.company_name, th.product_id FROM transaction_history AS th\n" +
          "            UNION ALL\n" +
          "            SELECT tx.amount, tx.company_name, tx.product_id FROM transaction AS tx\n" +
          "        ) AS total\n" +
          "        GROUP BY total.company_name, total.product_id\n" +
          "    ),\n" +
          "    ranked_orders AS (\n" +
          "        SELECT total_orders.company_name, total_orders.product_id, total_orders.total_amount,\n" +
          "               ROW_NUMBER() OVER (PARTITION BY company_name ORDER BY total_amount DESC) AS rank\n" +
          "        FROM total_orders\n" +
          "    )\n" +
          "    SELECT ranked_orders.company_name, ranked_orders.product_id\n" +
          "    FROM ranked_orders\n" +
          "    WHERE rank = 1;";

  final String createPS = "INSERT INTO transaction_history (transaction_id, company_name, product_id, amount, created_date) VALUES(?,?,?,?,?)";

  final String getAllPS = "SELECT * FROM transaction_history";
  final String getInactiveCompaniesForGivenPeriodPS="select name from company where name not in (\n" +
          "\tSELECT company_name\n" +
          "\t\tFROM (\n" +
          "\t\t\tSELECT th.company_name,th.created_date FROM transaction_history AS th\n" +
          "\t\t\tUNION ALL\n" +
          "\t\t\tSELECT tx.company_name, tx.created_date FROM transaction AS tx\n" +
          "\t\t) AS totaltable\n" +
          "\twhere created_date>=:start_date and created_date<=:end_date\n" +
          ")\n";
  private final RowMapper<Pair> pairMapper = (resultSet, i) -> new Pair(
      resultSet.getString(1),
      resultSet.getInt(2)
  );

  private final RowMapper<String> stringMapper = (resultSet, i) -> resultSet.getString(1);

 /* private final RowMapper<Company> companyRowMapper = (resultSet, i) -> new Company()
          .name(resultSet.getString("name"));*/

  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  public List<Pair> query1() {
    return Objects.requireNonNull(getJdbcTemplate()).query(getMaxProductByCompanyPS, pairMapper);
  }

  public List<String> query2(Date start, Date end) {

    List<String> companies=new ArrayList<>();

    Map<String, Object> params = new HashMap<>();
    params.put("start_date", start);
    params.put("end_date", end);

    var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
    companies= template.query(getInactiveCompaniesForGivenPeriodPS, params, stringMapper);

    return companies;
  }

  public void deleteAll() {
    Objects.requireNonNull(getJdbcTemplate()).update("DELETE FROM transaction_history");
  }
}
