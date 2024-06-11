package org.ozyegin.cs.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import javax.sql.DataSource;

import org.checkerframework.checker.units.qual.C;
import org.ozyegin.cs.entity.Company;
import org.ozyegin.cs.entity.Email;
import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository extends JdbcDaoSupport {
  final int batchSize = 10;
  final String createPS = "INSERT INTO company (name,zip, country, city, street_info, phoneNumber) VALUES(?,?,?,?,?,?)";
  final String getSinglePS = "SELECT * FROM company WHERE name=?";
  final String getByCountryPS = "SELECT * FROM company WHERE country=:country";
  final String deletePS = "DELETE FROM company WHERE name=?";
  final String deleteAllPS = "DELETE FROM company";
  final String findBySameZipsWithDifferentCity = "SELECT * FROM company WHERE city<>? and zip=?";


  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }

  @Autowired
  private EmailRepository emailRepository;

  private final RowMapper<Company> companyRowMapper = (resultSet, i) -> new Company()
          .name(resultSet.getString("name"))
          .zip(resultSet.getInt("zip"))
          .country(resultSet.getString("country"))
          .city(resultSet.getString("city"))
          .streetInfo(resultSet.getString("street_info"))
          .phoneNumber(resultSet.getString("phoneNumber"));


  /*public Company find(String name) {
    return null;
  }*/
  public void findBySameZipsWithDifferentCity(String city,int zip) throws Exception {

    Company company=new Company();
    try {
      company= Objects.requireNonNull(getJdbcTemplate()).queryForObject(findBySameZipsWithDifferentCity,
              new Object[] {city,zip},
              companyRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return;
    }

    if(company.getName()!=null)
    {
      throw new Exception("There is a company with same zip but different city!");
    }
  }

  public Company find(String name) throws EmptyResultDataAccessException {
    Company company=new Company();
    try {
      company= Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSinglePS,
              new Object[] {name},
              companyRowMapper);
    } catch (EmptyResultDataAccessException e) {
      throw new EmptyResultDataAccessException(0);
      // return null; // Return null indicating company not found

    }

    List<String> s=new ArrayList<>();
    List<Email> e = emailRepository.findByCompanyName(company.getName());
    for (Email email : e) {
      s.add(email.getEmail());
    }
    company.setE_mails(s);


    return company;

  }




  public List<Company> findByCountry(String country) {

    List<Company> companies=new ArrayList<>();

    Map<String, Object> params = new HashMap<>();
    params.put("country", country);
    var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
    companies= template.query(getByCountryPS, params, companyRowMapper);


    for (Company company : companies) {
      List<String> s = new ArrayList<>();
      List<Email> e = emailRepository.findByCompanyName(company.getName());
      for (Email email : e) {
        s.add(email.getEmail());
      }
      company.setE_mails(s);
    }
    return companies;
  }

  public String create(Company company) throws Exception {

    findBySameZipsWithDifferentCity(company.getCity(),company.getZip());

    List<Company> companies = new ArrayList<>();
    companies.add(company);
    Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, companies,
            batchSize,
            (ps, company1) -> {
              ps.setObject(1, company1.getName(), Types.VARCHAR); // Assuming product_id is an INTEGER
              ps.setObject(2, company1.getZip(), Types.INTEGER);
              ps.setObject(3, company1.getCountry(), Types.VARCHAR);
              ps.setObject(4, company1.getCity(), Types.VARCHAR);
              ps.setObject(5, company1.getStreetInfo(), Types.VARCHAR);
              ps.setObject(6, company1.getPhoneNumber(), Types.VARCHAR);
            });
    List<Integer> a =emailRepository.create(company.getE_mails(),company.getName());

    return companies.get(0).getName();
  }



  public String delete(String name) {
    emailRepository.delete(name);
    Objects.requireNonNull(getJdbcTemplate()).update(deletePS,name) ;
    return name;
  }
  public void deleteAll() {
    emailRepository.deleteAll();
   Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
  }

}
