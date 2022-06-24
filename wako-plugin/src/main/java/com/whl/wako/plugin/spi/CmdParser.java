package com.whl.wako.plugin.spi;

public interface CmdParser<I,O> {
  O parse(I input);
}
