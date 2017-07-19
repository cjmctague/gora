/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.gora.aerospike.query;

import com.aerospike.client.Record;
import com.aerospike.client.query.RecordSet;
import org.apache.gora.aerospike.store.AerospikeStore;
import org.apache.gora.persistency.Persistent;
import org.apache.gora.query.Query;
import org.apache.gora.query.impl.ResultBase;
import org.apache.gora.store.DataStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Aerospike specific implementation of the {@link org.apache.gora.query.Result}
 * interface.
 */
public class AerospikeQueryResult<K, T extends Persistent> extends ResultBase<K, T> {

  private List<AerospikeResultRecord> resultRecords;

  private String[] fields;

  public AerospikeQueryResult(DataStore<K, T> dataStore, Query<K, T> query,
          List<AerospikeResultRecord> recordsList, String[] fields) {
    super(dataStore, query);
    this.resultRecords = recordsList;
    this.fields = fields;
  }

  @Override
  public float getProgress() throws IOException, InterruptedException {
    //ToDo: to be implemented
    return 0;
  }

  /**
   * Method to get the Aerospike specific data store
   *
   * @return the Aerospike data store instance
   */
  @Override
  public AerospikeStore getDataStore() {
    return (AerospikeStore) super.getDataStore();
  }

  /**
   * {@inheritDoc}
   *
   * @return true if more elements exist
   * @throws IOException if we reach non-existent result
   */
  @Override
  protected boolean nextInner() throws IOException {
    if (offset < 0 || offset > (resultRecords.size() - 1)) {
      return false;
    }
    key = (K) resultRecords.get((int) this.offset).getKey().userKey;
    persistent = (T) getDataStore()
            .createPersistentInstance(resultRecords.get((int) this.offset).getRecord(), fields);
    return true;
  }
}