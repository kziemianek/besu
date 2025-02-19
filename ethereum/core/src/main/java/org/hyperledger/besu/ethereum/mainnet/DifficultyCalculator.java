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
package org.hyperledger.besu.ethereum.mainnet;

import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.BlockHeader;

import java.math.BigInteger;

/** Calculates block difficulties. */
@FunctionalInterface
public interface DifficultyCalculator<C> {

  /**
   * Calculates the block difficulty for a block.
   *
   * @param time the time the block was generated
   * @param parent the block's parent block header
   * @param context the context in which the difficulty calculator should operate
   * @return the block difficulty
   */
  BigInteger nextDifficulty(long time, BlockHeader parent, ProtocolContext<C> context);
}
