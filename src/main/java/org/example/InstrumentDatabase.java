package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstrumentDatabase {
    public Map<String, String[]> instrumentKeywords = new HashMap<String, String[]>();

    InstrumentDatabase(){
        instrumentKeywords.put("bass", new String[]{"\\bbass\\b", "\\bbasowa\\b", "\\bbas\\b"});
        instrumentKeywords.put("flute", new String[]{"\\bflute\\b", "\\bflet\\b", "\\bfl\\b", "\\bfl.\\b"});
        instrumentKeywords.put("drums", new String[]{"\\bdrums\\b", "\\bdrum\\b", "\\bpercussion\\b", "\\bperc\\b", "\\bperkusja\\b"});
        instrumentKeywords.put("violin", new String[]{"\\bviolin\\b", "\\bskrzypce\\b"});
        instrumentKeywords.put("trumpet", new String[]{"\\btrumpet\\b", "\\btrąbka\\b", "\\btrabka\\b", "\\btr\\b", "\\btr.\\b"});
        instrumentKeywords.put("trombone", new String[]{"\\btrombone\\b", "\\bpuzon\\b", "\\btrom\\b"});
        instrumentKeywords.put("alt_saxophone", new String[]{"\\baltowy\\b", "\\balt\\b", "\\balto\\b"});
        instrumentKeywords.put("tenor_saxophone", new String[]{"\\btenor\\b", "\\btenorowy\\b", "\\bten\\b"});
        instrumentKeywords.put("baritone_saxophone", new String[]{"\\bbaritone\\b", "\\bbarytonowy\\b", "\\bbaryton\\b"});
        instrumentKeywords.put("horn", new String[]{"\\bhorn\\b", "\\bwaltornia\\b", "\\bróg\\b", "\\bhrn\\b"});
        instrumentKeywords.put("oboe", new String[]{"\\boboe\\b", "\\bobój\\b", "\\bob\\b"});
        instrumentKeywords.put("clarinet", new String[]{"\\bclarinet\\b", "\\bklarnet\\b", "\\bcl\\b"});
        instrumentKeywords.put("guitar", new String[]{"\\bguitar\\b", "\\bgitara\\b", "\\bgit\\b", "\\bguit\\b", "\\bgt\\b", "\\bgt.\\b"});
        instrumentKeywords.put("piano", new String[]{"\\bpiano\\b", "\\bpno\\b", "\\bpianino\\b", "\\bfortepian\\b"});
    }

    public String findInstrument(String text) {
        text = text.toLowerCase();
        for (Map.Entry<String, String[]> instrument : instrumentKeywords.entrySet()) {
            for (String keyword : instrument.getValue()) {
                Pattern pattern = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    return instrument.getKey();
                }
            }
        }
        return "unknown";
    }
}
