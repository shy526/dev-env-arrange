package com.github.shy526.devenvarrange.download;

import com.github.shy526.devenvarrange.oo.ToolRoute;
import com.github.shy526.devenvarrange.oo.ToolVersion;

import java.nio.file.Path;
import java.util.List;

public interface DownloadProcess {
    List<ToolVersion> getVersion(ToolRoute toolRoute);
    Path downloadFile(ToolRoute toolRoute, String version, String path);
}
