package net.legacylauncher.bootstrap.meta;

import com.github.zafarkhaja.semver.Version;
import net.legacylauncher.bootstrap.BuildConfig;
import net.legacylauncher.bootstrap.json.ToStringBuildable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LocalBootstrapMeta extends ToStringBuildable implements BootstrapMeta {
    private static final Version VERSION = Version.parse(BuildConfig.VERSION);
    private static final LocalBootstrapMeta INSTANCE = new LocalBootstrapMeta();

    public static LocalBootstrapMeta getInstance() {
        return INSTANCE;
    }

    private String shortBrand = BuildConfig.SHORT_BRAND;

    @Override
    public Version getVersion() {
        return VERSION;
    }

    @Override
    public String getShortBrand() {
        return shortBrand;
    }

    public void setShortBrand(String shortBrand) {
        this.shortBrand = shortBrand;
    }

    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
                .append("version", getVersion())
                .append("shortBrand", getShortBrand());
    }
}
