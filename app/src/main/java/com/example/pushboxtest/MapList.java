package com.example.pushboxtest;

public class MapList {
    public static int[][][] map = {
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
            }
    };

    public static int count = map.length;

    public static int getCount() {
        return count;
    }

    public static int[][] getMap(int grade) {
        int[][] temp;
        int[][] result;
        if (grade >= 0 && grade < count)
            temp = map[grade];
        else
            temp = map[0];
        int row = temp.length;
        int column = temp[0].length;
        result = new int[row][column];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                result[i][j] = temp[i][j];
        return result;
    }

}
