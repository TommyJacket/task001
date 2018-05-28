import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Владимир Плиска
 * @version 1.0
 *
 * Класс, выполняющий подсчет статистики в соотвествие с ГОСТ 21073.
 */
class stats_gost {
    /**
     * avgDensity - среднее число зерен в 1 кв.мм
     * avgScore - балл зерна (средний)
     * maxScore - балл зерна (максимальный)
     * minScore - балл зерна (минимальный)
     * medScore - балл зерна (медианный)
     * avgDiameter - средний диаметр зерна, мкм
     */
    private static long avgDensity, avgScore;
    private static int maxScore, minScore;
    private static double medScore, avgDiameter;

    /**
     * Основной метод для обсчета изображения.
     *
     * @param inputList массив объектов типа Polygon
     * @param imageArea площадь изображения
     * @return JSONObject с посчитанной статистикой
     */
    static JSONObject calculateStats(List<Polygon> inputList, int imageArea){
        List<Double> blobDiameters = new ArrayList<>();
        List<Integer> blobScores = new ArrayList<>();


        for (Polygon blob: inputList){
            blobDiameters.add(blob.getEqCircleDiameter());
        }

        getBlobsScores(blobDiameters, blobScores);

        maxScore = Collections.min(blobScores); // Судя по заданию - должно быть так
        minScore = Collections.max(blobScores);
        avgScore = getAverageBlobScore(blobScores);
        medScore = (blobScores.get(blobScores.size()/2) + blobScores.get(blobScores.size()/2 - 1))/2;
        avgDiameter = getAverageBlobDiameter(blobDiameters);
        avgDensity = getAverageDensity(imageArea, inputList.size());

        JSONObject resultObj = new JSONObject();
        assembleJson(resultObj);

        return resultObj;
    }

    /**
     * Метод, высчитывающий баллы зерен
     *
     * @param blobDiameters массив диаметров объектов
     * @param blobScores массив баллов объектов
     */
    private static void getBlobsScores(List<Double> blobDiameters, List<Integer> blobScores) {
        for (Double blobDiameter: blobDiameters) {
            if (blobDiameter <= 0.002) {
                blobScores.add(14);
            } else if (blobDiameter <= 0.004) {
                blobScores.add(13);
            } else if (blobDiameter <= 0.005) {
                blobScores.add(12);
            } else if (blobDiameter <= 0.007) {
                blobScores.add(11);
            } else if (blobDiameter <= 0.010) {
                blobScores.add(10);
            } else if (blobDiameter <= 0.015) {
                blobScores.add(9);
            } else if (blobDiameter <= 0.02) {
                blobScores.add(8);
            } else if (blobDiameter <= 0.03) {
                blobScores.add(7);
            } else if (blobDiameter <= 0.04) {
                blobScores.add(6);
            } else if (blobDiameter <= 0.06) {
                blobScores.add(5);
            } else if (blobDiameter <= 0.08) {
                blobScores.add(4);
            } else if (blobDiameter <= 0.12) {
                blobScores.add(2);
            } else if (blobDiameter <= 0.2) {
                blobScores.add(1);
            } else if (blobDiameter <= 0.3) {
                blobScores.add(0);
            } else if (blobDiameter <= 0.5) {
                blobScores.add(-1);
            } else if (blobDiameter <= 0.7) {
                blobScores.add(-2);
            } else if (blobDiameter <= 1) {
                blobScores.add(-3);
            }
        }
    }

    /**
     * Метод, высчитывающий средний балл зерна
     *
     * @param blobScores массив баллов объектов
     * @return округленный средний балл
     */
    private static long getAverageBlobScore(List<Integer> blobScores) {
        double sum = 0;

        for (int blobScore: blobScores) {
            sum += blobScore;
        }

        return Math.round(sum/blobScores.size());
    }

    /**
     * Метод, высчитывающий средний диаметр зерна
     *
     * @param blobDiameters массив диаметров объектов
     * @return средний диаметр пятна
     */
    private static Double getAverageBlobDiameter(List<Double> blobDiameters) {
        double sum = 0;

        for (double blobDiameter: blobDiameters) {
            sum += blobDiameter;
        }

        return sum/blobDiameters.size();
    }

    /**
     * Метод, высчитывающий среднее число зерен в 1 кв. мм
     *
     * @param imageArea площадь изображения
     * @param areaDensity количество пятен на изображении
     * @return приблизительная плотность пятен на изображении прощадью 100 мкм
     */
    private static long getAverageDensity(int imageArea, int areaDensity) {
        final int estimateFor = 1000000;

        if (imageArea == estimateFor) {
            return areaDensity;
        } else {
            double areaRatio = (double)estimateFor / (double)imageArea;

            return Math.round(areaDensity * areaRatio);
        }
    }

    /**
     * Метод, выполняющий сборку JSON объекта
     *
     * @param toAssemble ссылка на JSON объект, который требуется заполнить
     */
    private static void assembleJson(JSONObject toAssemble) {
        JSONArray stats = new JSONArray()
                .put(
                        new JSONArray()
                                .put("Балл зерна (средний)")
                                .put("" + avgScore)
                )
                .put(
                        new JSONArray()
                                .put("Средний диаметр зерна, мкм")
                                .put("" + avgDiameter)
                )
                .put(
                        new JSONArray()
                                .put("Среднее число зерен в 1 кв.мм")
                                .put("" + avgDensity)
                )
                .put(
                        new JSONArray()
                                .put("Балл зерна (минимальный)")
                                .put("" + minScore)
                )
                .put(
                        new JSONArray()
                                .put("Балл зерна (медианный)")
                                .put("" + medScore)
                )
                .put(
                        new JSONArray()
                                .put("Балл зерна (максимальный)")
                                .put("" + maxScore)
                );

        JSONObject opt = new JSONObject()
                .put("tableColor", "ada")
                .put("borders", true)
                .put("tableSize", 14)
                .put("tableColWidth", 4261)
                .put("tableFontFamily", "Times New Roman")
                .put("tableAlign", "left");

        toAssemble.put("val", stats);
        toAssemble.put("opt", opt);
        toAssemble.put("name", "Таблица 1. Результаты анализа величина зерна по ГОСТ 21073");
        toAssemble.put("type", "table");
    }
}
