public class Normalizer {
    public String lang;

    public Normalizer(String lang) {
        this.lang = lang;

    }

    public DateTime convert_datetime_vi(DateTime entity) {
        if (entity.raw == null) {
            return entity;
        }
        Date today = Date.now("Asia/Bangkok");
        Date output = new Date();

        return entity;
    }
}
