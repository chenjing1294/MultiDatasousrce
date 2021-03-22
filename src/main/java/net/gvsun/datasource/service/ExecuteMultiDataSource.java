package net.gvsun.datasource.service;

import net.gvsun.datasource.GvsunDataSource;
import net.gvsun.datasource.dto.Result;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

import java.util.concurrent.Callable;

public class ExecuteMultiDataSource implements Callable {
    private boolean baselineOnMigrate;
    private GvsunDataSource dataSource;

    public ExecuteMultiDataSource(GvsunDataSource dataSource, boolean baselineOnMigrate) {
        this.dataSource = dataSource;
        this.baselineOnMigrate = baselineOnMigrate;
    }

    @Override
    public Result<String> call() {
        Result<String> res = new Result<>(0, "success");
        try {
            FluentConfiguration configure = Flyway.configure();
            configure.validateOnMigrate(false);
            configure.baselineOnMigrate(baselineOnMigrate);
            Flyway flyway = configure.dataSource(dataSource).load();
            flyway.migrate();
        } catch (Exception e) {
            res.setCode(1);
            res.setMsg(e.getMessage());
        }
        return res;
    }
}
