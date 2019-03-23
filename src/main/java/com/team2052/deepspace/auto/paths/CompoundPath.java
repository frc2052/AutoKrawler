package com.team2052.deepspace.auto.paths;

import java.util.ArrayList;
import java.util.List;

public abstract class CompoundPath {
    List<Path> paths = new ArrayList<Path>();

    public List<Path> getPaths(){
        return paths;
    }

    protected void addPath(Path path){
        paths.add(paths.size(),path);
    }
}
