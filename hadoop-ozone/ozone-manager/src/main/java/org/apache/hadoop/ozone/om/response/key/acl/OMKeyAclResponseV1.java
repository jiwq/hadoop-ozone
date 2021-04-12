/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.om.response.key.acl;

import org.apache.hadoop.hdds.utils.db.BatchOperation;
import org.apache.hadoop.ozone.om.OMMetadataManager;
import org.apache.hadoop.ozone.om.helpers.OmDirectoryInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.request.file.OMFileRequest;
import org.apache.hadoop.ozone.om.response.CleanupTableInfo;
import org.apache.hadoop.ozone.protocol.proto.OzoneManagerProtocolProtos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.apache.hadoop.ozone.om.OmMetadataManagerImpl.DIRECTORY_TABLE;
import static org.apache.hadoop.ozone.om.OmMetadataManagerImpl.FILE_TABLE;

/**
 * Response for Bucket acl request for layout version V1.
 */
@CleanupTableInfo(cleanupTables = { FILE_TABLE, DIRECTORY_TABLE })
public class OMKeyAclResponseV1 extends OMKeyAclResponse {

  private boolean isDirectory;

  public OMKeyAclResponseV1(
      @NotNull OzoneManagerProtocolProtos.OMResponse omResponse,
      @NotNull OmKeyInfo omKeyInfo, boolean isDirectory) {
    super(omResponse, omKeyInfo);
    this.isDirectory = isDirectory;
  }

  /**
   * For when the request is not successful.
   * For a successful request, the other constructor should be used.
   *
   * @param omResponse
   */
  public OMKeyAclResponseV1(
      @NotNull OzoneManagerProtocolProtos.OMResponse omResponse) {
    super(omResponse);
  }

  @Override public void addToDBBatch(OMMetadataManager omMetadataManager,
      BatchOperation batchOperation) throws IOException {

    String ozoneDbKey = omMetadataManager
        .getOzonePathKey(getOmKeyInfo().getParentObjectID(),
            getOmKeyInfo().getFileName());
    if (isDirectory) {
      OmDirectoryInfo dirInfo = OMFileRequest.getDirectoryInfo(getOmKeyInfo());
      omMetadataManager.getDirectoryTable()
          .putWithBatch(batchOperation, ozoneDbKey, dirInfo);
    } else {
      omMetadataManager.getKeyTable()
          .putWithBatch(batchOperation, ozoneDbKey, getOmKeyInfo());
    }
  }
}