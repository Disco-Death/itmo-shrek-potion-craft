package com.potion.ISPotion.utils;

import java.util.Arrays;
import java.util.function.Function;

import static java.lang.Math.*;
import static java.lang.String.format;

public final class AsymptoticComplexityUtil {
    private AsymptoticComplexityUtil(){}
    public static String getAsymptoticComplexity(Iterable<DataPoint> timeSeries) {
        var asymptoticComplexityResult = new AsymptoticComplexityResult("O(?)", 1e10);

        for (var asymptoticFunction: asymptoticFunctions) {
            asymptoticComplexityResult = TestAsymptoticComplexityFunction(
                    timeSeries,
                    asymptoticFunction.Func,
                    asymptoticComplexityResult.minError,
                    asymptoticFunction.Name,
                    asymptoticComplexityResult.asymptoticComplexity);
        }

        return format(
                "Рост: %s, мин. ошибка: %.6f",
                asymptoticComplexityResult.asymptoticComplexity,
                asymptoticComplexityResult.minError);
    }

    public record DataPoint(double abscissa, double ordinate) {}
    private record AsymptoticFunction(String Name, Function<Double, Double> Func) {}
    private record AsymptoticComplexityResult(String asymptoticComplexity, double minError) {}

    private static AsymptoticComplexityResult TestAsymptoticComplexityFunction(
            Iterable<DataPoint> timeSeries,
            Function<Double, Double> func,
            double minError,
            String currComplexity,
            String ComplexityWithMinError) {
        double c0 = 1e-10, c1 = 1e-10;
        var isInit = false;
        for (var dataPoint: timeSeries) {
            if (dataPoint.abscissa < 1e-10)
                break;
            var divRes = LimDiv(dataPoint.ordinate, func.apply(dataPoint.abscissa));
            if (divRes <= 0.0 || dataPoint.abscissa < 2.0)
                continue;
            if (!isInit) {
                c0 = divRes;
                c1 = divRes;
                isInit = true;
            }
            if (c0 > divRes)
                c0 = divRes;
            if (c1 < divRes)
                c1 = divRes;
        }
        var currError = abs(1.0 - LimDiv(c0, c1));
        if (currError < minError) {
            minError = currError;
            ComplexityWithMinError = currComplexity;
        }
        return new AsymptoticComplexityResult(ComplexityWithMinError, minError);
    }

    private static double LimDiv(double a, double b) {
        final double limit = 1e-10;
        return (abs(b) > limit) ? a / b : 0.0;
    }

    private static final Iterable<AsymptoticFunction> asymptoticFunctions = Arrays.asList(
            new AsymptoticFunction("Константный", (n) -> 1.0),
            new AsymptoticFunction(
                    "O(log(log(log(n)))) [тройной логарифмический]",
                    (n) -> log(log(log(n)))),
            new AsymptoticFunction(
                    "O(log(log(n))) [двойной логарифмический]",
                    (n) -> log(log(n))),
            new AsymptoticFunction(
                    "O(log(n)) [логарифмический]",
                    Math::log),
            new AsymptoticFunction(
                    "O(log^2(n)) [полилогарифмический]",
                    (n) -> pow(log(n), 2)),
            new AsymptoticFunction(
                    "O(log^3(n)) [полилогарифмический]",
                    (n) -> pow(log(n), 3)),
            new AsymptoticFunction(
                    "O(log^4(n)) [полилогарифмический]",
                    (n) -> pow(log(n), 4)),
            new AsymptoticFunction(
                    "O(n^0.5) [сублинейный]",
                    Math::sqrt),
            new AsymptoticFunction(
                    "O(n) [линейный]",
                    (n) -> n),
            new AsymptoticFunction(
                    "O(n*log(n)) [линейно-логарифмический]",
                    (n) -> n * log(n)),
            new AsymptoticFunction(
                    "O(n*log^2(n)) [линейно-логарифмический]",
                    (n) -> n * pow(log(n), 2)),
            new AsymptoticFunction(
                    "O(n*log^3(n)) [линейно-логарифмический]",
                    (n) -> n * pow(log(n), 3)),
            new AsymptoticFunction(
                    "O(n*log^4(n)) [линейно-логарифмический]",
                    (n) -> n * pow(log(n), 4)),
            new AsymptoticFunction(
                    "O(n^2) [квадратичный]",
                    (n) -> pow(n, 2)),
            new AsymptoticFunction(
                    "O(n^2*log(n)) [квадратично-логарифмический]",
                    (n) -> pow(n, 2) * log(n)),
            new AsymptoticFunction(
                    "O(n^2*log^2(n)) [квадратично-логарифмический]",
                    (n) -> pow(n, 2) * pow(log(n), 2)),
            new AsymptoticFunction(
                    "O(n^2*log^3(n)) [квадратично-логарифмический]",
                    (n) -> pow(n, 2) * pow(log(n), 3)),
            new AsymptoticFunction(
                    "O(n^2*log^4(n)) [квадратично-логарифмический]",
                    (n) -> pow(n, 2) * pow(log(n), 4)),
            new AsymptoticFunction(
                    "O(n^3) [кубический]",
                    (n) -> pow(n, 3)),
            new AsymptoticFunction(
                    "O(n^3*log(n)) [кубически-логарифмический]",
                    (n) -> pow(n, 3) * log(n)),
            new AsymptoticFunction(
                    "O(n^3*log^2(n)) [кубически-логарифмический]",
                    (n) -> pow(n, 3) * pow(log(n), 2)),
            new AsymptoticFunction(
                    "O(n^3*log^3(n)) [кубически-логарифмический]",
                    (n) -> pow(n, 3) * pow(log(n), 3)),
            new AsymptoticFunction(
                    "O(n^3*log^4(n)) [кубически-логарифмический]",
                    (n) -> pow(n, 3) * pow(log(n), 4)),
            new AsymptoticFunction(
                    "O(n^4) [степенной]",
                    (n) -> pow(n, 4))
    );

}
