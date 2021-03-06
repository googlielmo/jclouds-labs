/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.docker.suppliers;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import org.jclouds.docker.DockerApiMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.location.Provider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

@Singleton
public class DockerSSLContextSupplier implements Supplier<SSLContext> {
   private final Supplier<Credentials> creds;
   private final String caCertPath;


   @Inject
   DockerSSLContextSupplier(@Provider Supplier<Credentials> creds, @Named(DockerApiMetadata.DOCKER_CA_CERT_PATH) String caCertPath) {
      this.creds = creds;
      this.caCertPath = caCertPath;
   }

   @Override
   public SSLContext get() {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      try {
         SSLContextBuilder builder = new SSLContextBuilder();
         builder.clientKeyAndCertificate(currentCreds.credential, currentCreds.identity);
         if (!Strings.isNullOrEmpty(caCertPath)) {
            builder.caCertificate(caCertPath);
         }
         return builder.build();
      } catch (GeneralSecurityException e) {
         throw propagate(e);
      } catch (IOException e) {
         throw propagate(e);
      }
   }

}
