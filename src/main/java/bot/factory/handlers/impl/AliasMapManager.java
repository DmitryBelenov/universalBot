package bot.factory.handlers.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AliasMapManager {
    public static Map</*user id*/Integer, /*key for nearby locations*/String> locationKeysMap = Collections.synchronizedMap(new HashMap<>());
    public static Map</*user id*/Integer, /*alternative location handling state*/YoStates> yoStatesMap = Collections.synchronizedMap(new HashMap<>());
    public static Map</*user id*/Integer, /*state*/MyWorldStates> myWorldStatesMap = Collections.synchronizedMap(new HashMap<>());
    public static Map</*user id*/Integer, /*state*/VNoteStates> videoNoteStatesMap = Collections.synchronizedMap(new HashMap<>());
}
