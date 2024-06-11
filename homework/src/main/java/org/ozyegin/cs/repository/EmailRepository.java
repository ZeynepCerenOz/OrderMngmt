package org.ozyegin.cs.repository;

import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Email;
import org.ozyegin.cs.entity.Product;
import org.ozyegin.cs.entity.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

@Repository
public class EmailRepository extends JdbcDaoSupport {
  final int batchSize = 10;
  final String createPS = "INSERT INTO email ( email, company_name) VALUES(?,?)";
  final String getSinglePS = "SELECT * FROM email WHERE email_id=?";

  final String getSingleCompanyPS = "SELECT * FROM email WHERE company_name=:company_name";
  final String deletePS = "DELETE FROM email WHERE company_name=?";
  final String deleteAllPS = "DELETE FROM email";


  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  private final RowMapper<Email> emailRowMapper = (resultSet, i) -> new Email()
          .emailId(resultSet.getInt("email_id"))
          .email(resultSet.getString("email"))
          .companyName(resultSet.getString("company_name"));


  /*public Company find(String name) {
    return null;
  }*/
  public Email find(int id) {
    try {
      return Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSinglePS,
              new Object[] {id},
              emailRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return null; // Return null indicating company not found
    }
  }


  public List<Email> findByCompanyName(String companyName) {
    Map<String, Object> params = new HashMap<>();
    params.put("company_name", companyName);
    var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
    return template.query(getSingleCompanyPS, params, emailRowMapper);
  }


  public List<Integer> create(List<String> emails, String companyName) {
    List<Integer> generatedIds = new ArrayList<>();
    List<Email> emailsStructured=new ArrayList<>();

    for(int i=0;i<emails.size();i++){
      Email e=new Email();
      e.setEmail(emails.get(i));
      emailsStructured.add(e);
    }

    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, emailsStructured,
            batchSize,
            (ps, email) -> {
             // ps.setObject(1, email.getEmailId(), Types.INTEGER); // Assuming product_id is an INTEGER
              ps.setObject(1, email.getEmail(), Types.VARCHAR);
              ps.setObject(2, companyName, Types.VARCHAR);
            });

    List<Email> eList=findByCompanyName(companyName);
    for(int i=0;i<eList.size();i++){
      generatedIds.add(eList.get(i).getEmailId());
    }
    return generatedIds;
  }

  public void delete(String name) {
    Objects.requireNonNull(getJdbcTemplate()).update(deletePS,name) ;

  }
  public void deleteAll() {

    Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);

  }


}
