package de.variamc.velocitysystem.objects.report;

import de.variamc.velocitysystem.VelocitySystem;
import de.variamc.velocitysystem.objects.user.User;
import net.kyori.adventure.text.Component;

/**
 * Class created by Kaseax on 2022
 */
public class ReportService {

    private static volatile ReportService instance;

    private final ReportCache reports;

    public ReportService() {
        instance = this;
        this.reports = new ReportCache();
    }

    public ReportCache getReports() {
        return reports;
    }

    public static ReportService getInstance() {
        return instance;
    }

    public Report getReport(String abuserName) {
        return this.reports.stream().filter(report -> report.getAbuserName().equalsIgnoreCase(abuserName)).findFirst().orElse(null);
    }

    public Report getReportById(String id) {
        return this.reports.stream().filter(report -> report.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void createReport(String senderName, String abuserName, String id, ReportReasons reportReasons) {
        this.reports.add(new Report(abuserName, senderName, id, reportReasons));

        for (User user : VelocitySystem.getInstance().getUsers()) {
            if (user.getPlayer().hasPermission("variamc.team.report")) {
                user.getPlayer().sendMessage(Component.text("§8§m-----------------------------§r"));
                user.getPlayer().sendMessage(Component.text("      §e§lNEW REPORT"));
                user.getPlayer().sendMessage(Component.text(""));
                user.getPlayer().sendMessage(Component.text("§7Reportet§8: §e" + abuserName));
                user.getPlayer().sendMessage(Component.text("§7Sender§8: §e" + senderName));
                user.getPlayer().sendMessage(Component.text("§7ID§8: §e" + id));
                user.getPlayer().sendMessage(Component.text("§7Reported for§8: §e" + reportReasons.name()));
                user.getPlayer().sendMessage(Component.text(""));
                user.getPlayer().sendMessage(Component.text("§8§m-----------------------------§r"));
            }
        }
    }

    public void removeReport(Report report) {
        this.reports.remove(report);
    }

    public void removeReportsById(String id) {
        this.reports.remove(this.reports.stream().filter(report -> report.getId().equals(id)).findFirst().orElse(null));
    }

    public void removeReports(String abuserName) {
        this.reports.remove(this.reports.stream().filter(report -> report.getAbuserName().equalsIgnoreCase(abuserName)).findFirst().orElse(null));
    }
}
