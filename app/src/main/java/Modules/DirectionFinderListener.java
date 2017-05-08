package Modules;

import java.util.List;

/**
 * Created by Atif on 11/27/16.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> route);
}
