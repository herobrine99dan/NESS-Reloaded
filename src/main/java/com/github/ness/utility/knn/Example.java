package com.github.ness.utility.knn;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Example {

    public static void main(String[] args) throws IOException {
        System.out.println("Please Write the Train File Name!");
        Scanner scanner = new Scanner(System.in);
        File trainfile = new File(".", scanner.nextLine());
        System.out.println("Please Write the Test File Name!");
        File testfile = new File(".", scanner.nextLine());
        List<DataSet> sets = Utility.createDataSetListByCSVFile(trainfile);
        Knn alghorithm = new Knn(sets, 1, true);
        System.out.println("Prediction: " + alghorithm.predict(Utility.getTestFile(testfile)));
        scanner.close();
    }

}
