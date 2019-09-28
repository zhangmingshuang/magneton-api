package com.magneton.api2.core;

import com.beust.jcommander.JCommander;
import com.magneton.api2.commander.ApiCommander;
import com.magneton.api2.commander.CommonApiCommander;
import com.magneton.api2.spi.SpiServices;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author zhangmingshuang
 * @since 2019/9/28
 */
@Getter
public class ApiForeman {

    public static ApiForeman contactWorker(String[] args) {

        ApiForeman apiForeman = new ApiForeman();

        CommonApiCommander commonApiCommander = new CommonApiCommander();
        apiForeman.setBasicApiCommander(commonApiCommander);

        Map<String, Object> apiWorkers = SpiServices.getServices(ApiWorker.class);
        if (apiWorkers == null || apiWorkers.size() < 1) {
            throw new RuntimeException("can't found api worker.");
        }

        JCommander commander = new JCommander(commonApiCommander);

        Map<String, ApiCommander> commanders = new HashMap<>();
        apiWorkers.forEach((name, worker) -> {
            ApiCommander apiCommander = ((ApiWorker) worker).apiCommander();
            if (apiCommander == null) {
                return;
            }
            commander.addCommand(name, apiCommander);
            apiForeman.addWorker(name, (ApiWorker) worker, apiCommander);
        });

        commander.parse(args);

        if (commonApiCommander.isHelp()) {
            commander.usage();
            System.exit(0);
        }

        String parsedCommand = commander.getParsedCommand();

        ApiCommander apiCommander = commanders.get(parsedCommand);
        apiForeman.setCommand(parsedCommand);
        return apiForeman;
    }

    private String command;
    private CommonApiCommander commonApiCommander;
    private Map<String, ApiWorker> apiWorkers = new HashMap<>();
    private Map<String, ApiCommander> apiCommanders = new HashMap<>();

    private void setCommand(String command) {
        this.command = command;
    }

    private void setBasicApiCommander(CommonApiCommander commonApiCommander) {
        this.commonApiCommander = commonApiCommander;
    }

    private void addWorker(String name, ApiWorker apiWorker, ApiCommander apiCommander) {
        apiWorkers.put(name, apiWorker);
        apiCommanders.put(name, apiCommander);
    }

    public ApiWorker getApiWorker() {
        return this.apiWorkers.get(command);
    }

    public ApiCommander getApiCommander() {
        return this.apiCommanders.get(command);
    }
}
