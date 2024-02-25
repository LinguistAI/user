package app.linguistai.bmvp.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "xp")
public class XPConfiguration {
    private Map<String, Integer> values;
    private Map<String, Integer> levels;

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

    public void setValues(Map<String, Integer> values) {
        this.values = values;
    }

    public void setLevels(Map<String, Integer> levels) {
        this.levels = levels;
    }
}
