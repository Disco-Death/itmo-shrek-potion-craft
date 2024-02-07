package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Stat;
import com.potion.ISPotion.repo.*;
import com.potion.ISPotion.utils.AsymptoticComplexityUtil.DataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

import static com.potion.ISPotion.utils.AsymptoticComplexityUtil.getAsymptoticComplexity;

@Component
public class StatsComponent {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private StorageCellRepository storageCellRepository;
    @Autowired
    private ReportRepository reportRepository;

    public Iterable<Stat> getStats() {
        var stat = new ArrayList<Stat>();

        var usersTimeSeries = new ArrayList<DataPoint>();
        var users = userRepository.findAllByOrderByCreationDateAsc().stream().toList();
        var i = 1;
        var count = 0;
        var lastDate = new Date(0);
        for (var user: users) {
            var creationDate = user.getCreationDate();
            if (creationDate == null)
                continue;
            if (creationDate.after(lastDate)) {
                usersTimeSeries.add(new DataPoint(i, count));
                lastDate = creationDate;
                count = 0;
                i++;
            } else {
                count++;
            }
        }
        var usersStat = new Stat("Пользователи: ", getAsymptoticComplexity(usersTimeSeries));
        stat.add(usersStat);

        var salesTimeSeries = new ArrayList<DataPoint>();
        var sales = saleRepository.findAllByOrderByCreationDateAsc().stream().toList();
        i = 1;
        count = 0;
        lastDate = new Date(0);
        for (var sale: sales) {
            var creationDate = sale.getCreationDate();
            if (creationDate == null)
                continue;
            if (creationDate.after(lastDate)) {
                salesTimeSeries.add(new DataPoint(i, count));
                lastDate = creationDate;
                count = 0;
                i++;
            } else {
                count++;
            }
        }
        var salesStat = new Stat("Продажи: ", getAsymptoticComplexity(salesTimeSeries));
        stat.add(salesStat);

        var storageCellsTimeSeries = new ArrayList<DataPoint>();
        var storageCells = storageCellRepository.findAllByOrderByCreationDateAsc().stream().toList();
        i = 1;
        count = 0;
        lastDate = new Date(0);
        for (var storageCell: storageCells) {
            var creationDate = storageCell.getCreationDate();
            if (creationDate == null)
                continue;
            if (creationDate.after(lastDate)) {
                storageCellsTimeSeries.add(new DataPoint(i, count));
                lastDate = creationDate;
                count = 0;
                i++;
            } else {
                count++;
            }
        }
        var storageCellsStat = new Stat("Склад: ", getAsymptoticComplexity(storageCellsTimeSeries));
        stat.add(storageCellsStat);

        var reportsTimeSeries = new ArrayList<DataPoint>();
        var reports = reportRepository.findAllByOrderByCreationDateAsc().stream().toList();
        i = 1;
        count = 0;
        lastDate = new Date(0);
        for (var report: reports) {
            var creationDate = report.getCreationDate();
            if (creationDate == null)
                continue;
            if (creationDate.after(lastDate)) {
                reportsTimeSeries.add(new DataPoint(i, count));
                lastDate = creationDate;
                count = 0;
                i++;
            } else {
                count++;
            }
        }
        var reportsStat = new Stat("Отчёты: ", getAsymptoticComplexity(reportsTimeSeries));
        stat.add(reportsStat);

        return stat;
    }
}
