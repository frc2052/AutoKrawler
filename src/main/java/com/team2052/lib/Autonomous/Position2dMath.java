package com.team2052.lib.Autonomous;

/**
 * Created by KnightKrawler on 9/12/2018.
 */
public class Position2dMath {

    public static Position2d addCartisian(Position2d first, Position2d second){
        return new Position2d(first.forward + second.forward, first.lateral + second.lateral);
    }

    public static Position2d subtractCartisian(Position2d first, Position2d second){
        return new Position2d(first.forward - second.forward, first.lateral - second.lateral);
    }
}
