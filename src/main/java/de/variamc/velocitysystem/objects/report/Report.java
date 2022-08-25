package de.variamc.velocitysystem.objects.report;

/**
 * Class created by Kaseax on 2022
 */
public class Report {

    private final String abuserName;
    private final String senderName;
    private final String id;
    private ReportReasons reportReasons;

    public Report(String abuserName, String senderName, String id, ReportReasons reportReasons) {
        this.abuserName = abuserName;
        this.senderName = senderName;
        this.id = id;
        this.reportReasons = reportReasons;
    }

    public void setReportReasons(ReportReasons reportReasons) {
        this.reportReasons = reportReasons;
    }

    public ReportReasons getReportReasons() {
        return reportReasons;
    }

    public String getId() {
        return id;
    }

    public String getAbuserName() {
        return abuserName;
    }

    public String getSenderName() {
        return senderName;
    }
}
