

	/**
	 * Access and manage a Google Tag Manager account.
	 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Account;
import com.google.api.services.tagmanager.model.BuiltInVariable;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.ContainerVersion;
import com.google.api.services.tagmanager.model.CreateContainerVersionRequestVersionOptions;
import com.google.api.services.tagmanager.model.CreateContainerVersionResponse;
import com.google.api.services.tagmanager.model.ListAccountsResponse;
import com.google.api.services.tagmanager.model.ListContainerVersionsResponse;
import com.google.api.services.tagmanager.model.ListContainersResponse;
import com.google.api.services.tagmanager.model.ListVariablesResponse;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Tag;
import com.google.api.services.tagmanager.model.Trigger;
import com.google.api.services.tagmanager.model.Variable;
import com.google.api.services.tagmanager.model.Workspace;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;



	public class GTM_API 
	{
		
		  // Path to client_secrets.json file downloaded from the Developer's Console.
		  // The path is relative to GTM_APITest.java.
		  private static final String CLIENT_SECRET_JSON_RESOURCE = "client_secrets.json";

		  // The directory where the user's credentials will be stored for the application.
		  private static final File DATA_STORE_DIR = new File("");

		  private static final String APPLICATION_NAME = "GTM_API";
		  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
		  private static NetHttpTransport httpTransport;
		  private static FileDataStoreFactory dataStoreFactory;
		  private static TagManager manager;
		  private static final String ALL_PAGES_TRIGGER_ID = "2147479553";
		  private static HashMap<String, String> triggerMap;
		  private static final boolean debug = true;
		  
		  public static void main(String[] args) throws IOException 
		  {
			  try 
			  {
			      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			      // Authorization flow.
			      Credential credential = authorize();
			      manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
			          .setApplicationName(APPLICATION_NAME).build();
			      
			      TagManager test = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
				          .setApplicationName(APPLICATION_NAME).build();			      
			    } catch (Exception e) 
			  	{
			      e.printStackTrace();
			    }
			  
			 cloneExampleContainer("Onboarding Project - Greg", "4131139637", "GTM-5G7WXC3");
			  
			}

			private static Credential authorize() throws Exception 
			{
				// Load client secrets.
				GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
						new InputStreamReader(GTM_API.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

				// Set up authorization code flow for all auth scopes.
				GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
						JSON_FACTORY, clientSecrets, TagManagerScopes.all()).setDataStoreFactory(dataStoreFactory)
						.build();

				// Authorize.
				return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
			}

			private static void cloneExampleContainer(String containerName, String accountId, String exampleContainerPublicId) {

				String accountPath = "accounts/" + accountId;
				String testContainerPath = "accounts/4131139637/containers/9959443";
				Container exampleContainer = new Container();

				try {
					
					//Get example container
					if(debug) {
						exampleContainer = manager.accounts().containers().get(testContainerPath).execute();
					}else {
						List<Account> allAccounts = manager.accounts().list().execute().getAccount();

						if(allAccounts != null && !allAccounts.isEmpty()) {
							for(Account account : allAccounts) {
								List<Container> accountContainers = manager.accounts().containers().list(account.getAccountId()).execute().getContainer();
								if(accountContainers != null && !accountContainers.isEmpty()) {
									long matchingContainersFound = accountContainers.stream().filter(c-> c.getPublicId().equals(exampleContainerPublicId)).count();
									if (matchingContainersFound > 0) {
										exampleContainer = accountContainers.stream().filter(c-> c.getPublicId().equals(exampleContainerPublicId)).collect(Collectors.toList()).get(0);
										break;
									}
								}
								Thread.sleep(2000);
							}
						}
						
					}
					
					//Find container or create new
					if(!exampleContainer.isEmpty()) {
						
						//Loop through containers on specified account to check if new container already exists
						List<Container> existingAccountContainers = manager.accounts().containers().list(accountPath).execute().getContainer();
						Container activeContainer = new Container();

						if(existingAccountContainers != null && !existingAccountContainers.isEmpty()) {
							for (Container accountContainer : existingAccountContainers) {
								if(accountContainer.getName().equals(containerName)) {
									activeContainer = accountContainer.clone();
									break;
								}
							}
						}
						
						// Create container if not found
						if(activeContainer.isEmpty()) {
							System.out.println("----- Create Container -----");
							activeContainer.setAccountId(accountPath).setName(containerName).setUsageContext(Arrays.asList("web"));
							activeContainer = manager.accounts().containers().create(accountPath, activeContainer).execute();
							System.out.println(containerName + " created");
						}
						
						//Copy variables, triggers, tags
						Workspace exampleWks = manager.accounts().containers().workspaces().list(exampleContainer.getPath()).execute().getWorkspace().get(0);
						Workspace activeWks = manager.accounts().containers().workspaces().list(activeContainer.getPath()).execute().getWorkspace().get(0);
						
						enableBuiltInVariables(exampleWks.getPath(), activeWks.getPath());
						copyVariables(exampleWks.getPath(), activeWks.getPath());
						copyTriggers(exampleWks.getPath(), activeWks.getPath());
						copyTags(exampleWks.getPath(), activeWks.getPath());
						publishContainer(activeWks.getPath());
						
					}
					
				}catch (GoogleJsonResponseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			private static void enableBuiltInVariables(String fromWorkspacePath, String toWorkspacePath) {
				try {
					
					List<BuiltInVariable> exampleContainerBuiltInVariables = manager.accounts().containers().workspaces().builtInVariables().list(fromWorkspacePath).execute().getBuiltInVariable();
					List<BuiltInVariable> activeContainerBuiltInVariables = manager.accounts().containers().workspaces().builtInVariables().list(toWorkspacePath).execute().getBuiltInVariable();
					
					for(BuiltInVariable abv: activeContainerBuiltInVariables) {
						exampleContainerBuiltInVariables.removeIf(ebv->ebv.getName().equals(abv.getName()));
					}
					
					List<String> bvToEnable = exampleContainerBuiltInVariables.stream().map(bv -> bv.getType()).collect(Collectors.toList());
					manager.accounts().containers().workspaces().builtInVariables().create(toWorkspacePath).setType(bvToEnable).execute();
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private static void copyVariables(String fromContainerPath, String toContainerPath) {
				try {
					System.out.println("----- Create Variables -----");
					List<Variable> exampleContainerVariables = manager.accounts().containers().workspaces().variables().list(fromContainerPath).execute().getVariable();
					List<Variable> activeContainerVariables = manager.accounts().containers().workspaces().variables().list(toContainerPath).execute().getVariable();
					
					if(exampleContainerVariables != null && !exampleContainerVariables.isEmpty()) {
						for(Variable existingVariable : exampleContainerVariables) {
							if(activeContainerVariables == null || !activeContainerVariables.stream().anyMatch(acv->acv.getName().equals(existingVariable.getName()))) {
								Variable newVariable = existingVariable.clone();
								newVariable.setAccountId(null).setContainerId(null).setFingerprint(null).setVariableId(null);
								newVariable = manager.accounts().containers().workspaces().variables().create(toContainerPath, newVariable).execute();
								System.out.println(newVariable.getName() + " created");
								Thread.sleep(1000);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}

			private static void copyTriggers(String fromContainerPath, String toContainerPath) {
				try {
					System.out.println("----- Create Triggers -----");
					triggerMap = new HashMap<>();

					List<Trigger> exampleContainerTriggers = manager.accounts().containers().workspaces().triggers().list(fromContainerPath).execute().getTrigger();
					List<Trigger> activeContainerTriggers = manager.accounts().containers().workspaces().triggers().list(toContainerPath).execute().getTrigger();
					
					//REVIEW for more efficient way to associate all activeContainerTriggers with the related existingContainerTriggers
					if(activeContainerTriggers != null && !activeContainerTriggers.isEmpty()) {
						for(Trigger act : activeContainerTriggers) {
							for(Trigger ect : exampleContainerTriggers) {
								if(act.getName().equals(ect.getName())) {
									triggerMap.put(ect.getTriggerId(), act.getTriggerId());
								}
							}
						}
					}
					
					
					if(exampleContainerTriggers != null && !exampleContainerTriggers.isEmpty()) {
						for (Trigger existingTrigger : exampleContainerTriggers) {
							if(activeContainerTriggers == null || !activeContainerTriggers.stream().anyMatch(acv->acv.getName().equals(existingTrigger.getName()))) {
								Thread.sleep(1000);
								Trigger newTrigger = existingTrigger.clone();
								newTrigger.setAccountId(null).setContainerId(null).setFingerprint(null).setTriggerId(null).setUniqueTriggerId(null);
								if(existingTrigger.getTriggerId().equals(ALL_PAGES_TRIGGER_ID)) {
									newTrigger.setTriggerId(ALL_PAGES_TRIGGER_ID);
								}
								newTrigger = manager.accounts().containers().workspaces().triggers().create(toContainerPath, newTrigger).execute();
								System.out.println(newTrigger.getName() + " (" + newTrigger.getTriggerId() + ") created");
								triggerMap.put(existingTrigger.getTriggerId(), newTrigger.getTriggerId());
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}

			private static void copyTags(String fromContainerPath, String toContainerPath) {
				try {
					System.out.println("----- Create Tags -----");

					List<Tag> exampleContainerTags = manager.accounts().containers().workspaces().tags().list(fromContainerPath).execute().getTag();
					List<Tag> activeContainerTags = manager.accounts().containers().workspaces().tags().list(toContainerPath).execute().getTag();
					
					if(exampleContainerTags != null && !exampleContainerTags.isEmpty()) {
						for (Tag existingTag : exampleContainerTags) {
							if(activeContainerTags == null || !activeContainerTags.stream().anyMatch(acv->acv.getName().equals(existingTag.getName()))) {
								Thread.sleep(1000);
								Tag newTag = existingTag.clone();
								newTag.set("parentFolderId",null);
								newTag.set("setupTag",null);
								newTag.set("teardownTag",null);
								newTag.setAccountId(null).setContainerId(null).setFingerprint(null).setTagId(null).setBlockingTriggerId(null).setFiringTriggerId(null);
			
								//Associate new triggers to tag
								List<String> newBlockingTriggers = new ArrayList<String>();
								List<String> newFiringTriggers = new ArrayList<String>();
			
								List<String> existingBlockingTriggers = existingTag.getBlockingTriggerId();
								if(existingBlockingTriggers != null && !existingBlockingTriggers.isEmpty()) {
									for(String blockingTriggerId : existingBlockingTriggers) {
										if(blockingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) {
											newBlockingTriggers.add(ALL_PAGES_TRIGGER_ID);
										}else {
											newBlockingTriggers.add(triggerMap.get(blockingTriggerId));
										}
			
									}
								}
			
								List<String> existingFiringTriggers = existingTag.getFiringTriggerId();
								if(existingFiringTriggers != null && !existingFiringTriggers.isEmpty()) {
									for(String firingTriggerId : existingFiringTriggers) {
										if(firingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) {
											newFiringTriggers.add(ALL_PAGES_TRIGGER_ID);
										}else {
											newFiringTriggers.add(triggerMap.get(firingTriggerId));
										}
			
									}
								}
			
								if(!newBlockingTriggers.isEmpty()) { newTag.setBlockingTriggerId(newBlockingTriggers);}
								if(!newFiringTriggers.isEmpty()) { newTag.setFiringTriggerId(newFiringTriggers);}
			
								newTag = manager.accounts().containers().workspaces().tags().create(toContainerPath, newTag).execute();
								System.out.println(newTag.getName() + " created");
						}

						}
						System.out.println("----- Tag Creation Complete -----");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}

			private static void publishContainer(String containerPath) {
				try {
					
					System.out.println("----- Create Container Version & Publish -----");
					CreateContainerVersionRequestVersionOptions options = new  CreateContainerVersionRequestVersionOptions();
					options.setName("Sample Container Version").setNotes("Sample Container Version");
					CreateContainerVersionResponse response = manager.accounts().containers().workspaces().createVersion(containerPath, options).execute();
					ContainerVersion version = response.getContainerVersion();
					if (version != null) {
						System.out.println("Container Version Id = " + version.getContainerVersionId());
						System.out.println("Container Version Fingerprint = " + version.getFingerprint());
						manager.accounts().containers().versions().publish(version.getPath());
						System.out.println("Container version " + version.getContainerVersionId() + " created and published");
					}
					System.out.println("----- Container Copy Complete -----");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	