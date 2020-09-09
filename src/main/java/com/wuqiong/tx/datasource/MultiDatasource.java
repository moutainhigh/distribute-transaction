package com.wuqiong.tx.datasource;

import com.wuqiong.tx.context.ApplicationContextHelper;
import com.wuqiong.tx.context.ContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description
 * @Author Cain
 * @date 2020/8/27
 */
public class MultiDatasource extends AbstractRoutingDataSource {

    private Log log = LogFactory.getLog(MultiDatasource.class);

    @Override
    protected DataSource determineTargetDataSource() {
        //DataSource dataSource = super.determineTargetDataSource();
        DataSource dataSource = null;
        if (dataSource == null) {
            try {
                return getDBRoute(ContextHolder.getCompanyID(), ContextHolder.getApplicationType());
            } catch (SQLException e) {
                log.error("查询数据库源出错", e);
            }
        }
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ContextHolder.getCompanyID()+":"+ContextHolder.getApplicationType();
    }

    private DataSource getDBRoute(String companyID, int type) throws SQLException {
        String sql = "select i.alias,i.username,i.pass,i.url,i.port,i.driver from db_route r,db_instance i where r.dbInstance=i.id and r.companyID='"+companyID+"' and r.type="+type;
        DataSource systemDataSource = ApplicationContextHelper.getBeanByNameAndType("defaultDatasource", DataSource.class);
        Connection connection = null;
        PreparedStatement pt = null;
        try {
            connection = systemDataSource.getConnection();
            pt = connection.prepareStatement(sql);
            ResultSet rs = pt.executeQuery();
            while(rs.next()) {
                String aliase = rs.getString("alias");
                String username = rs.getString("username");
                String password = rs.getString("pass");
                String url = rs.getString("url");
                Long port = rs.getLong("port");
                String driver = rs.getString("driver");
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName(driver);
                dataSource.setUrl("jdbc:mysql://"+url+":"+port+"/"+aliase);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                return dataSource;
            }
        } catch (SQLException e) {
            log.error("查询公司数据库路由出错", e);
        } finally {
            if (pt != null) {
                pt.clearParameters();
                pt.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }
}
