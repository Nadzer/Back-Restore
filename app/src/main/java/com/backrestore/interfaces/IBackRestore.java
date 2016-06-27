package com.backrestore.interfaces;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nadzer
 * 29/03/2016.
 */
public interface IBackRestore extends IPermissions {
    void setUI();

    List<HashMap<String, String>> getData();
}