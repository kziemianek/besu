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
package org.hyperledger.besu.consensus.ibft.messagedata;

import org.hyperledger.besu.consensus.ibft.messagewrappers.RoundChange;
import org.hyperledger.besu.ethereum.p2p.rlpx.wire.MessageData;
import org.hyperledger.besu.util.bytes.BytesValue;

public class RoundChangeMessageData extends AbstractIbftMessageData {

  private static final int MESSAGE_CODE = IbftV2.ROUND_CHANGE;

  private RoundChangeMessageData(final BytesValue data) {
    super(data);
  }

  public static RoundChangeMessageData fromMessageData(final MessageData messageData) {
    return fromMessageData(
        messageData, MESSAGE_CODE, RoundChangeMessageData.class, RoundChangeMessageData::new);
  }

  public RoundChange decode() {
    return RoundChange.decode(data);
  }

  public static RoundChangeMessageData create(final RoundChange signedPayload) {

    return new RoundChangeMessageData(signedPayload.encode());
  }

  @Override
  public int getCode() {
    return MESSAGE_CODE;
  }
}
