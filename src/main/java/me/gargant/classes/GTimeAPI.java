package me.gargant.classes;

import lombok.Getter;
import me.gargant.data.DataRepository;
import me.gargant.services.RunService;

public class GTimeAPI {
    @Getter
    private DataRepository dataRepository;
    @Getter
    private RunService runService;

    public GTimeAPI(DataRepository dataRepository, RunService runService) {
        this.dataRepository = dataRepository;
        this.runService = runService;
    }
}
