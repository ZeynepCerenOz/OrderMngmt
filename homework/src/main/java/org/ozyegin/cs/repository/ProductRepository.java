package org.ozyegin.cs.repository;

import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.*;

@Repository
public class ProductRepository extends JdbcDaoSupport {
  final int batchSize = 10;

  final String createPS = "INSERT INTO product (product_id, name, description,band_name) VALUES(?,?,?,?)";
    final String updatePS = "UPDATE product SET name=?, description=?, band_name=? WHERE product_id=?";

    final String getAllPS = "SELECT * FROM product";
    final String getSinglePS = "SELECT * FROM product WHERE product_id=?";
    final String getPS = "SELECT * FROM product WHERE product_id IN (:product_ids)";
    final String findByBrandNamePS = "SELECT * FROM product WHERE band_name = (:band_name)";
    final String deletePS = "DELETE FROM product WHERE product_id=?";
    final String findMaxProductIdPS="SELECT MAX(product_id) AS max_product_id  FROM product" ;
    final String deleteAllPS = "DELETE FROM product";

  // Mandatory for all repository
  @Autowired
  public void setDatasource(DataSource dataSource) {
    super.setDataSource(dataSource);
  }


  /**
   * Inserts the given products into product table.
   *
   * @param products the product list to insert into product table
   */
  private final RowMapper<Product> productRowMapper = (resultSet, i) -> new Product()
          .id(resultSet.getInt("product_id"))
          .name(resultSet.getString("name"))
          .description(resultSet.getString("description"))
          .brandName(resultSet.getString("band_name"));

    private final RowMapper<Product> productMaxIdRowMapper = (resultSet, i) -> new Product()
            .id(resultSet.getInt("max_product_id"));




    public List<Integer> create(List<Product> products) {
       List<Integer> generatedIds = new ArrayList<>();

        int maxId=findMaxProductIdPS();

       for(int i=0;i<products.size();i++){
           products.get(i).setId(maxId+i+1);
       }

       Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, products,
               batchSize,
               (ps, product) -> {
                   ps.setObject(1, product.getId(), Types.INTEGER); // Assuming product_id is an INTEGER
                   ps.setObject(2, product.getName(), Types.VARCHAR);
                   ps.setObject(3, product.getDescription(), Types.VARCHAR);
                   ps.setObject(4, product.getBrandName(), Types.VARCHAR);
               });
        for(Product product : products){
            generatedIds.add(product.getId());
        }

       return generatedIds;
   }



    public List<Product> getAll() {
        return Objects.requireNonNull(getJdbcTemplate()).query(getAllPS, productRowMapper);
    }
    public void update(List<Product> products) {
        Objects.requireNonNull(getJdbcTemplate()).batchUpdate(updatePS, products,
                batchSize,
                (ps, product) -> {
                    ps.setObject(1, product.getName(), Types.VARCHAR);
                    ps.setObject(2, product.getDescription(), Types.VARCHAR);
                    ps.setObject(3, product.getBrandName(), Types.VARCHAR);
                    ps.setObject(4, product.getId(), Types.INTEGER);
                });

        for (Product product : products) {
            System.out.println("Product updated successfully for ID: " + product.getId());
        }
    }
    public List<Product> findMultiple(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        } else {
            Map<String, List<Integer>> params = new HashMap<>() {
                {
                    this.put("product_ids", new ArrayList<>(ids));
                }
            };
            var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
            return template.query(getPS, params, productRowMapper);
        }
    }

    public Product find(int id) {
        try {
            return Objects.requireNonNull(getJdbcTemplate()).queryForObject(getSinglePS,
                    new Object[] {id},
                    productRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null indicating product not found
        }
    }

    public int findMaxProductIdPS(){
       List<Product> products= Objects.requireNonNull(getJdbcTemplate()).query(findMaxProductIdPS, productMaxIdRowMapper);

       return products.get(0).getId();

    }

    public List<Product> findByBrandName(String brandName) {
        Map<String, Object> params = new HashMap<>();
        params.put("band_name", brandName);
        var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
        return template.query(findByBrandNamePS, params, productRowMapper);
    }
    public void delete(List<Integer> ids) {
       for (int id : ids) {
            int rowsAffected = Objects.requireNonNull(getJdbcTemplate()).update(deletePS, id);
        }
    }
    public void deleteAll() {

       Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    }




}
