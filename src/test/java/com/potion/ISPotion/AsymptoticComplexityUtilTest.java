package com.potion.ISPotion;

import com.potion.ISPotion.utils.AsymptoticComplexityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class AsymptoticComplexityUtilTest {
    @Test
    public void testGetAsymptoticComplexity() {
        var timeSeries = Arrays.asList(
                new AsymptoticComplexityUtil.DataPoint(1, 1),
                new AsymptoticComplexityUtil.DataPoint(2, 4),
                new AsymptoticComplexityUtil.DataPoint(3, 9),
                new AsymptoticComplexityUtil.DataPoint(4, 16),
                new AsymptoticComplexityUtil.DataPoint(5, 25)
        );

        var expected = "Сложность: O(n^2) [квадратичная], Величина ошибки = 0,000000";

        var actual = AsymptoticComplexityUtil.getAsymptoticComplexity(timeSeries);

        Assertions.assertEquals(expected, actual);
    }
}
