/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.hyperledger.besu.cli.util;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import picocli.CommandLine;
import picocli.CommandLine.AbstractParseResultHandler;
import picocli.CommandLine.DefaultExceptionHandler;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.CommandLine.Model.IGetter;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.RunLast;

@RunWith(MockitoJUnitRunner.class)
public class ConfigOptionSearchAndRunHandlerTest {

  private static final String CONFIG_FILE_OPTION_NAME = "--config";
  @Rule public final TemporaryFolder temp = new TemporaryFolder();

  @Rule public ExpectedException exceptionRule = ExpectedException.none();

  private final ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
  private final ByteArrayOutputStream commandErrorOutput = new ByteArrayOutputStream();
  private final PrintStream outPrintStream = new PrintStream(commandOutput);
  private final PrintStream errPrintStream = new PrintStream(commandErrorOutput);

  private final AbstractParseResultHandler<List<Object>> resultHandler =
      new RunLast().useOut(outPrintStream).useAnsi(Ansi.OFF);
  private final DefaultExceptionHandler<List<Object>> exceptionHandler =
      new DefaultExceptionHandler<List<Object>>().useErr(errPrintStream).useAnsi(Ansi.OFF);
  private final Map<String, String> environment = singletonMap("BESU_LOGGING", "ERROR");
  private final ConfigOptionSearchAndRunHandler configParsingHandler =
      new ConfigOptionSearchAndRunHandler(
          resultHandler, exceptionHandler, CONFIG_FILE_OPTION_NAME, environment, false);

  @Mock ParseResult mockParseResult;
  @Mock CommandLine mockCommandLine;
  @Mock OptionSpec mockConfigOptionSpec;
  @Mock IGetter mockConfigOptionGetter;

  @Before
  public void initMocks() {
    final List<CommandLine> commandLines = new ArrayList<>();
    commandLines.add(mockCommandLine);
    when(mockParseResult.asCommandLineList()).thenReturn(commandLines);
    final List<String> originalArgs = new ArrayList<>();
    originalArgs.add(CONFIG_FILE_OPTION_NAME);
    when(mockParseResult.originalArgs()).thenReturn(originalArgs);
    when(mockParseResult.matchedOption(CONFIG_FILE_OPTION_NAME)).thenReturn(mockConfigOptionSpec);
    when(mockParseResult.hasMatchedOption(CONFIG_FILE_OPTION_NAME)).thenReturn(true);
    when(mockConfigOptionSpec.getter()).thenReturn(mockConfigOptionGetter);
  }

  @Test
  public void handle() throws Exception {
    when(mockConfigOptionGetter.get()).thenReturn(temp.newFile());
    final List<Object> result = configParsingHandler.handle(mockParseResult);
    verify(mockCommandLine).setDefaultValueProvider(any(IDefaultValueProvider.class));
    verify(mockCommandLine).parseWithHandlers(eq(resultHandler), eq(exceptionHandler), anyString());
    assertThat(result).isEmpty();
  }

  @Test
  public void handleShouldRaiseExceptionIfNoFileParam() throws Exception {
    exceptionRule.expect(Exception.class);
    final String error_message = "an error occurred during get";
    exceptionRule.expectMessage(error_message);
    when(mockConfigOptionGetter.get()).thenThrow(new Exception(error_message));
    configParsingHandler.handle(mockParseResult);
  }

  @Test
  public void selfMustReturnTheHandler() {
    assertThat(configParsingHandler.self()).isSameAs(configParsingHandler);
  }

  @Test
  public void shouldRetrieveConfigFromEnvironmentWhenConfigFileSpecified() throws Exception {
    final IDefaultValueProvider defaultValueProvider =
        configParsingHandler.createDefaultValueProvider(
            mockCommandLine, Optional.of(new File("foo")));
    final String value = defaultValueProvider.defaultValue(OptionSpec.builder("--logging").build());
    assertThat(value).isEqualTo("ERROR");
  }

  @Test
  public void shouldRetrieveConfigFromEnvironmentWhenConfigFileNotSpecified() throws Exception {
    final IDefaultValueProvider defaultValueProvider =
        configParsingHandler.createDefaultValueProvider(mockCommandLine, Optional.empty());
    final String value = defaultValueProvider.defaultValue(OptionSpec.builder("--logging").build());
    assertThat(value).isEqualTo("ERROR");
  }
}
