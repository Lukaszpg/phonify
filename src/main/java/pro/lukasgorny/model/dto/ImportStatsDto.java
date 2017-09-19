package pro.lukasgorny.model.dto;

public class ImportStatsDto {

    private Double importTime;
    private Integer count;
    private Integer countPhone;
    private Integer countTablet;

    public Double getImportTime() {
        return importTime;
    }

    public void setImportTime(Double importTime) {
        this.importTime = importTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountPhone() {
        return countPhone;
    }

    public void setCountPhone(Integer countPhone) {
        this.countPhone = countPhone;
    }

    public Integer getCountTablet() {
        return countTablet;
    }

    public void setCountTablet(Integer countTablet) {
        this.countTablet = countTablet;
    }
}
