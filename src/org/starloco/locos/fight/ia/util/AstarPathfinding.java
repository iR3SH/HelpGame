package org.starloco.locos.fight.ia.util;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.HashMap;

public class AstarPathfinding {

    private java.util.Map<Integer, Node> openList = new HashMap<Integer, Node>();
    private java.util.Map<Integer, Node> closeList = new LinkedHashMap<Integer, Node>();
    private GameMap map;
    private Fight fight;
    private int cellStart;
    private int cellEnd;

    public AstarPathfinding(GameMap map, Fight fight, int cellStart, int cellEnd) {
        setMap(map);
        setFight(fight);
        setCellStart(cellStart);
        setCellEnd(cellEnd);
    }

    public ArrayList<GameCase> getShortestPath(int value) {
        Node nodeStart = new Node(getCellStart(), null);
        openList.put(getCellStart(), nodeStart);
        while (!openList.isEmpty() && (!closeList.containsKey(getCellEnd()))) {
            char[] dirs = {'b', 'd', 'f', 'h'};
            Node nodeCurrent = bestNode();
            if (nodeCurrent.getCellId() == getCellEnd()
                    && !PathFinding.cellArroundCaseIDisOccuped(getFight(), nodeCurrent.getCellId()))
                return getPath();
            addListClose(nodeCurrent);
            for (int loc0 = 0; loc0 < 4; loc0++) {
                int cell = PathFinding.getCaseIDFromDirrection(nodeCurrent.getCellId(), dirs[loc0], getMap());
                Node node = new Node(cell, nodeCurrent);
                if (getMap().getCase(cell) == null)
                    continue;
                if (!getMap().getCase(cell).isWalkable(true, true, -1)
                        && cell != getCellEnd())
                    continue;
                if (PathFinding.haveFighterOnThisCell(cell, getFight())
                        && cell != getCellEnd())
                    continue;
                if (closeList.containsKey(cell))
                    continue;
                if (openList.containsKey(cell)) {
                    if (openList.get(cell).getCountG() > getCostG(node)) {
                        nodeCurrent.setChild(openList.get(cell));
                        openList.get(cell).setParent(nodeCurrent);
                        openList.get(cell).setCountG(getCostG(node));
                        openList.get(cell).setHeristic(PathFinding.getDistanceBetween(getMap(), cell, getCellEnd()) * 10);
                        openList.get(cell).setCountF(openList.get(cell).getCountG()
                                + openList.get(cell).getHeristic());
                    }
                } else {
                    if (value == 0)
                        if (PathFinding.casesAreInSameLine(getMap(), cell, getCellEnd(), dirs[loc0], 70))
                            node.setCountF((node.getCountG() + node.getHeristic()) - 10);
                    openList.put(cell, node);
                    nodeCurrent.setChild(node);
                    node.setParent(nodeCurrent);
                    node.setCountG(getCostG(node));
                    node.setHeristic(PathFinding.getDistanceBetween(getMap(), cell, getCellEnd()) * 10);
                    node.setCountF(node.getCountG() + node.getHeristic());
                }
            }
        }
        return getPath();
    }

    private ArrayList<GameCase> getPath() {
        Node current = getLastNode(closeList);
        if (current == null)
            return null;
        ArrayList<GameCase> path = new ArrayList<GameCase>();
        java.util.Map<Integer, GameCase> path0 = new HashMap<Integer, GameCase>();
        for (int index = closeList.size(); current.getCellId() != getCellStart(); index--) {
            if (current.getCellId() == getCellStart())
                continue;
            path0.put(index, getMap().getCase(current.getCellId()));
            current = current.getParent();

        }
        int index = -1;
        while (path.size() != path0.size()) {
            index++;
            if (path0.get(index) == null)
                continue;
            path.add(path0.get(index));
        }
        return path;
    }

    private Node getLastNode(java.util.Map<Integer, Node> list) {
        Node node = null;
        for (Entry<Integer, Node> entry : list.entrySet()) {
            node = entry.getValue();
        }
        return node;
    }

    private Node bestNode() {
        int bestCountF = 150000;
        Node bestNode = null;
        for (Node node : openList.values()) {
            if (node.getCountF() < bestCountF) {
                bestCountF = node.getCountF();
                bestNode = node;
            }
        }
        return bestNode;
    }

    private void addListClose(Node node) {
        if (openList.containsKey(node.getCellId()))
            openList.remove(node.getCellId());
        if (!closeList.containsKey(node.getCellId()))
            closeList.put(node.getCellId(), node);
    }

    private int getCostG(Node node) {
        int costG = 0;
        while (node.getCellId() == getCellStart()) {
            node = node.getParent();
            costG += 10;
        }
        return costG;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public Fight getFight() {
        return fight;
    }

    public void setFight(Fight fight) {
        this.fight = fight;
    }

    public int getCellStart() {
        return cellStart;
    }

    public void setCellStart(int cellStart) {
        this.cellStart = cellStart;
    }

    public int getCellEnd() {
        return cellEnd;
    }

    public void setCellEnd(int cellEnd) {
        this.cellEnd = cellEnd;
    }

}
