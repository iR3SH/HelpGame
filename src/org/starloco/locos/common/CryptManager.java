package org.starloco.locos.common;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;

import java.util.ArrayList;
import java.util.List;

public class CryptManager {

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public final static char[] HASH = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '-', '_'};


    public String cellID_To_Code(int cellID) {

        int char1 = cellID / 64, char2 = cellID % 64;
        return HASH[char1] + "" + HASH[char2];
    }

    public static int cellCode_To_ID(String cellCode) {

        char char1 = cellCode.charAt(0), char2 = cellCode.charAt(1);
        int code1 = 0, code2 = 0, a = 0;

        while (a < HASH.length) {
            if (HASH[a] == char1)
                code1 = a * 64;
            if (HASH[a] == char2)
                code2 = a;
            a++;
        }
        return (code1 + code2);
    }

    public static int getIntByHashedValue(char c) {
        for (int a = 0; a < HASH.length; a++)
            if (HASH[a] == c)
                return a;
        return -1;
    }

    public static char getHashedValueByInt(int c) {
        return HASH[c];
    }

    public ArrayList<GameCase> parseStartCell(GameMap map, int num)
    {
      ArrayList<GameCase> list=null;
      String infos;
      if(!map.getPlaces().equalsIgnoreCase("-1"))
      {
        infos=map.getPlaces().split("\\|")[num];
        int a=0;
        list=new ArrayList<>();
        while(a<infos.length())
        {
          if(map.getCase((getIntByHashedValue(infos.charAt(a))<<6)+getIntByHashedValue(infos.charAt(a+1))).isWalkable(false))
            list.add(map.getCase((getIntByHashedValue(infos.charAt(a))<<6)+getIntByHashedValue(infos.charAt(a+1))));
          else
          {
            GameCase cell=map.getCase(map.getRandomFreeCellId());
            while(!cell.isWalkable(false)||cell==null)
              cell=map.getCase(map.getRandomFreeCellId());
            list.add(cell);
          }
          a=a+2;
        }
      }
      return list;
    }

    public List<GameCase> decompileMapData(GameMap map, String data, byte sniffed) {
        List<GameCase> cells = new ArrayList<>();
        for (int f = 0; f < data.length(); f += 10) {
            String mapData = data.substring(f, f + 10);
            List<Byte> cellInfos = new ArrayList<>();

            for (int i = 0; i < mapData.length(); i++)
                cellInfos.add((byte) getIntByHashedValue(mapData.charAt(i)));
            
            boolean activo=(cellInfos.get(0)&32)>>5!=0;
            byte level=(byte)(cellInfos.get(1)&15);
            int walkable = ((cellInfos.get(2) & 56) >> 3);
            boolean los = (cellInfos.get(0) & 1) != 0;
            byte caminable=(byte)((cellInfos.get(2)&56)>>3);// 0 = no, 1 = medio, 4 = si
            byte slope=(byte)((cellInfos.get(4)&60)>>2);

            int layerObject2 = ((cellInfos.get(0) & 2) << 12) + ((cellInfos.get(7) & 1) << 12) + (cellInfos.get(8) << 6) + cellInfos.get(9);
            boolean layerObject2Interactive = ((cellInfos.get(7) & 2) >> 1) != 0;
            int object = (layerObject2Interactive && sniffed == 0 ? layerObject2 : -1);

            cells.add(new GameCase(map,(short)(f/10),activo,caminable,level,slope,(walkable!=0&&!mapData.equalsIgnoreCase("bhGaeaaaaa")&&!mapData.equalsIgnoreCase("Hhaaeaaaaa")),los,object));
        }
        return cells;
    }
}
