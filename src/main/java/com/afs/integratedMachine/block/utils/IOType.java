package com.afs.integratedMachine.block.utils;

public enum IOType {
    ONLY_IN(true, false),
    ONLY_OUT(false, true),
    BOTH(true, true),
    DISABLED(false, false);
    public final boolean canInsert;
    public final boolean canExtract;

    IOType(boolean canInsert, boolean canExtract) {
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    public static IOType of(boolean insert, boolean extract) {
        if (insert)
            if (extract) return BOTH;
            else return ONLY_IN;
        else if (extract) return ONLY_OUT;
        else return DISABLED;
    }

    public IOType next(){
        return switch (this){
            case ONLY_IN -> ONLY_OUT;
            case ONLY_OUT -> BOTH;
            case BOTH -> DISABLED;
            case DISABLED -> ONLY_IN;
        };
    }
}
