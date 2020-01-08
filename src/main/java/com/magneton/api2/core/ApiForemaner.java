package com.magneton.api2.core;

import com.beust.jcommander.JCommander;
import com.magneton.api2.command.ApiCommand;
import com.magneton.api2.command.CommonApiCommand;
import com.magneton.api2.spi.SpiServices;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class ApiForemaner {

    public static ApiForemaner contactWorker(String[] args) {

        ApiForemaner apiForemaner = new ApiForemaner();

        CommonApiCommand commonApi = new CommonApiCommand();
        apiForemaner.setCommonApiCommand(commonApi);

        Map<String, Object> apiWorkers = SpiServices.getServices(ApiWorker.class);
        if (apiWorkers == null || apiWorkers.size() < 1) {
            throw new RuntimeException("can't found api worker.");
        }

        JCommander commander = new JCommander(commonApi);

        apiWorkers.forEach((name, worker) -> {
            ApiWorker apiWorker = (ApiWorker) worker;
            ApiCommand apiCommand = apiWorker.apiWorkCommand();
            if (apiCommand == null) {
                return;
            }
            commander.addCommand(name, apiCommand);
        });
        commander.parse(args);

        if (commonApi.isHelp()) {
            commander.usage();
            System.exit(0);
            return null;
        }

        //子命令
        String apiWorkerName = commander.getParsedCommand();
        Object apiWorker = apiWorkers.get(apiWorkerName);
        if (apiWorker == null) {
            throw new RuntimeException("can't found api worker, named: " + apiWorkerName);
        }
        return apiForemaner;
    }

    @Setter
    private CommonApiCommand commonApiCommand;
    @Setter
    @Getter
    private ApiWorker apiWorker;

}
