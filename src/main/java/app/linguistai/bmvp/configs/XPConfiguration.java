package app.linguistai.bmvp.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "xp")
public class XPConfiguration {
    private Map<String, Integer> values;
    private Map<String, Integer> levels;

    private Long baselevel;

    private Long levelcoefficient;

    public int getXP(String action) {
        return values.getOrDefault(action, 0);
    }

    public int getLevelThreshold(String level) {
        return levels.getOrDefault(level, 0);
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public Map<String, Integer> getLevels() {
        return levels;
    }

    public Long getBaseLevel() {
        return baselevel;
    }

    public Long getLevelCoefficient() {
        return levelcoefficient;
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }

    public void setLevels(Map<String, Integer> levels) {
        this.levels = levels;
    }

    public void setBaseLevel(Long baselevel) {
        this.baselevel = baselevel;
    }

    public void setLevelCoefficient(Long levelcoefficient) {
        this.levelcoefficient = levelcoefficient;
    }
}
