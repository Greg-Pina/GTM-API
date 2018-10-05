

	/**
	 * Access and manage a Google Tag Manager account.
	 */

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.*;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.*;

import org.apache.commons.codec.language.bm.Rule;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManager.Accounts;
import com.google.api.services.tagmanager.TagManager.Accounts.Containers;
import com.google.api.services.tagmanager.TagManager.Accounts.Containers.Get;
import com.google.api.services.tagmanager.TagManager.Accounts.Containers.Workspaces;
import com.google.api.services.tagmanager.TagManager.Accounts.Containers.Workspaces.CreateVersion;
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.*;
import com.google.api.services.tagmanager.model.Account;
import com.google.api.services.tagmanager.model.BuiltInVariable;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Trigger;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.ContainerVersion;
import com.google.api.services.tagmanager.model.CreateBuiltInVariableResponse;
import com.google.api.services.tagmanager.model.CreateContainerVersionRequestVersionOptions;
import com.google.api.services.tagmanager.model.CreateContainerVersionResponse;
import com.google.api.services.tagmanager.model.ListAccountsResponse;
import com.google.api.services.tagmanager.model.ListContainerVersionsResponse;
import com.google.api.services.tagmanager.model.ListContainersResponse;
import com.google.api.services.tagmanager.model.ListEnabledBuiltInVariablesResponse;
import com.google.api.services.tagmanager.model.ListTagsResponse;
import com.google.api.services.tagmanager.model.ListTriggersResponse;
import com.google.api.services.tagmanager.model.ListVariablesResponse;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Tag;
import com.google.api.services.tagmanager.model.UserPermission;
import com.google.api.services.tagmanager.model.Variable;
import com.google.api.services.tagmanager.model.Workspace;
import com.google.gson.JsonObject;

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
			  
			 cloneExampleContainer("Greg_Pina_Test_test_test", "56800", "GTM-PNJH2T");
			  
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

			
			@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
			private static void cloneExampleContainer(String containerName, String accountId, String exampleContainerPublicId) throws IOException
			{
				

				String accountPath = "accounts/" + accountId;
				Container exampleContainer = new Container();
				exampleContainer = manager.accounts().containers().get("accounts/38028818/containers/970849").execute();
				Workspace exampleWS = manager.accounts().containers().workspaces().get("accounts/38028818/containers/970849/workspaces/43/").execute();
				String ExampleWS_String = exampleWS.getPath().toString();
				try 
				{
					
					//Loop through containers on specified account to verify new container doesn't already exist
					List<Container> targetAccountContainers = manager.accounts().containers().list("accounts/" + accountId).execute().getContainer();
					Container createdContainer = new Container();

					if(targetAccountContainers != null && !targetAccountContainers.isEmpty())
					{
						for (Container accountContainer : targetAccountContainers)
						{
							if(accountContainer.getName().equals(containerName)) 
							{
								createdContainer = accountContainer.clone();
								System.out.println("Container exists.");
							}
						}
					}

					if(createdContainer.isEmpty()) 
					{
						// Get example container using publicContainerId
						List<Account> allAccounts = manager.accounts().list().execute().getAccount();

						if(allAccounts != null && !allAccounts.isEmpty())
						{
							System.out.println("Looping through all accounts...");
							System.out.println();
							System.out.println();
							for(Account account : allAccounts) 
							{
								System.out.println(account.getName());
								List<Container> accountContainers = manager.accounts().containers().list("accounts/" + account.getAccountId()).execute().getContainer();
								if(accountContainers != null && !accountContainers.isEmpty()) 
								{
									//long not int
									long matchingContainersFound = accountContainers.stream().filter(c-> c.getPublicId().equals(exampleContainerPublicId)).count();
									if (matchingContainersFound > 0) 
									{
										exampleContainer = accountContainers.stream().filter(c-> c.getPublicId().equals(exampleContainerPublicId)).collect(Collectors.toList()).get(0);
										break;
									}
								}
								Thread.sleep(2000);
							}
						}

						if(!exampleContainer.isEmpty()) 
						{
							Container newContainer = new Container();
							//Create new container under specific account
							System.out.println();
							System.out.println();
							System.out.println("Container Created");
							
						    newContainer = new Container();
							newContainer.setAccountId(accountPath);
							newContainer.setName(containerName);
							newContainer.setUsageContext(Arrays.asList("web"));
							newContainer = manager.accounts().containers().create(accountPath, newContainer).execute();
							System.out.println(containerName + " copied and created successfuly");
							Workspace newWS = manager.accounts().containers().workspaces().get(newContainer.getPath().toString()+"/workspaces/1").execute();

							String newWorkSpaceString = newWS.getPath().toString();
							
							
							System.out.println("-----------------------------------------------");
							System.out.println();
							
							

							
							//Copy Variables to New Container
							System.out.println("*** Copying Variables ***");
							System.out.println();
						    Variable newVariable = new Variable();
					        BuiltInVariable newBIVariable = new BuiltInVariable();
						    List<Variable> exampleVariables = manager.accounts().containers().workspaces().variables().list(ExampleWS_String).execute().getVariable();
							if(exampleVariables != null && !exampleVariables.isEmpty()) 
							{
								for(Variable createdVariable : exampleVariables) 
								{
									Thread.sleep(2000);
								    newVariable = createdVariable.clone();
									newVariable.getWorkspaceId();
									newVariable = manager.accounts().containers().workspaces().variables().create(newWorkSpaceString, newVariable).execute();
								
									System.out.println(newVariable.getName() + " created successfuly");
								}
							}
							
							
							List<BuiltInVariable> BIVariables = manager.accounts().containers().workspaces().builtInVariables().list(ExampleWS_String).execute().getBuiltInVariable();
							
							if(BIVariables != null && !BIVariables.isEmpty() )
							{
								
								for( BuiltInVariable BIVariable : BIVariables)
								{
									Thread.sleep(2000);
									
									newBIVariable = BIVariable.clone();
									
									List<BuiltInVariable> CreatedBIVariables = manager.accounts().containers().workspaces().builtInVariables().create(newWorkSpaceString).setType(Arrays.asList(BIVariable.getType())).execute().getBuiltInVariable();

									
									System.out.println( newBIVariable.getName() + " created successfully");
								}
							}
							
							
							System.out.println("-----------------------------------------------");
							System.out.println();
							
							
							//Copy triggers to new container
							 
							  System.out.println("*** Copying Triggers *** ");
							  System.out.println();
							  HashMap<String, String> triggerMap = new HashMap<>();
							
							List<Trigger> exampleTriggers = manager.accounts().containers().workspaces().triggers().list(ExampleWS_String).execute().getTrigger();
							if(exampleTriggers != null && !exampleTriggers.isEmpty()) 
							{
								for ( Trigger createdTrigger : exampleTriggers) 
								{
									Thread.sleep(2000);
									Trigger newTrigger = createdTrigger.clone();
									newTrigger.setAccountId(null).setContainerId(null).setFingerprint(null).setTriggerId(null).setUniqueTriggerId(null);
									if(createdTrigger.getTriggerId().equals(ALL_PAGES_TRIGGER_ID)) 
									{
										newTrigger.setTriggerId(ALL_PAGES_TRIGGER_ID);
									}
									newTrigger = manager.accounts().containers().workspaces().triggers().create(newWorkSpaceString, newTrigger).execute();
									System.out.println(newTrigger.getName() + " created successfuly");
									triggerMap.put(createdTrigger.getTriggerId(), newTrigger.getTriggerId());
									
									
								}
							}
							System.out.println("-----------------------------------------------");
							System.out.println();

							
							
							//Copy tags to new container, attaching appropriate triggers/rules
							
							System.out.println("*** Copying Tags *** ");
							System.out.println();
							List<Tag> exampleTags = manager.accounts().containers().workspaces().tags().list(ExampleWS_String).execute().getTag();
							if(exampleTags != null && !exampleTags.isEmpty()) 
							{
								for (Tag createdTag : exampleTags) 
								{
									Thread.sleep(2000);
									Tag newTag = createdTag.clone();
									newTag.set("parentFolderId",null);
									newTag.set("setupTag",null);
									newTag.set("teardownTag",null);
							newTag.setAccountId(null).setContainerId(null).setFingerprint(null).setTagId(null).setBlockingTriggerId(null).setFiringTriggerId(null);
							
							//Associate new triggers to tag
							List<String> newBlockingTriggers = new ArrayList<String>();
							List<String> newFiringTriggers = new ArrayList<String>();
							
							List<String> exampleBlockingTriggers = createdTag.getBlockingTriggerId();
							if(exampleBlockingTriggers != null && !exampleBlockingTriggers.isEmpty()) 
							{
								for(String blockingTriggerId : exampleBlockingTriggers) 
								{
									if(blockingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) 
									{
										newBlockingTriggers.add(ALL_PAGES_TRIGGER_ID);
									}else 
									{
										newBlockingTriggers.add(triggerMap.get(blockingTriggerId));
									}
							
								}
							}
						
						List<String> exampleFiringTriggers = createdTag.getFiringTriggerId();
						if(exampleFiringTriggers != null && !exampleFiringTriggers.isEmpty()) 
						{
							for(String firingTriggerId : exampleFiringTriggers) 
							{
								if(firingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) 
								{
									newFiringTriggers.add(ALL_PAGES_TRIGGER_ID);
								}else 
								{
									newFiringTriggers.add(triggerMap.get(firingTriggerId));
								}
						
							}
						}
						
						if(!newBlockingTriggers.isEmpty()) { newTag.setBlockingTriggerId(newBlockingTriggers);}
						if(!newFiringTriggers.isEmpty()) { newTag.setFiringTriggerId(newFiringTriggers);}
						
						newTag = manager.accounts().containers().workspaces().tags().create(newWorkSpaceString, newTag).execute();
						System.out.println(newTag.getName() + " created successfuly");
						
													
				
								}
							
							}
							
						System.out.println("Publishing Container Version");
						CreateContainerVersionRequestVersionOptions options = new  CreateContainerVersionRequestVersionOptions();
						options.setName("Published Version");
						options.setNotes("Published Version");
						
						CreateContainerVersionResponse response = manager.accounts().containers().workspaces().createVersion(newWorkSpaceString, options).execute();
						
						ContainerVersion version = response.getContainerVersion();
						manager.accounts().containers().versions().publish(newWorkSpaceString + "/version/" + version.getContainerVersionId()).execute();
						
						if( version != null )
						{
							System.out.println("Container Version ID = " + version.getContainerVersionId());
							System.out.println("Container Version Fingerprint = " + version.getFingerprint());
							manager.accounts().containers().versions().update(newWorkSpaceString + "/version/" + version.getContainerVersionId(), version.setName("Published Version")).execute();
						}
						
						
						System.out.println("-----------------------------------------------");
						System.out.println();			

						System.out.println("*** Container Successfully Copied & Published ***");
						
							
						}
					}
					
						
				}catch (GoogleJsonResponseException e) 
				{
					System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
					e.printStackTrace();
				} catch (IOException e) 
				{
					e.printStackTrace();
				} catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		

		  
		  private static Container CopyContainer(TagManager service, Container newContainer, String containerName, String accountPath) throws IOException 
		  {
				System.out.println("----- Create Container -----");
				//Create new container under specific account
			    newContainer = new Container();
				newContainer.setAccountId(accountPath);
				newContainer.setName(containerName);
				newContainer.setUsageContext(Arrays.asList("web"));
				newContainer = manager.accounts().containers().create(accountPath, newContainer).execute();
				System.out.println(containerName + " created successfuly");
				//System.out.println(newContainer.getPath().toString());
				Workspace newWS = manager.accounts().containers().workspaces().get(newContainer.getPath().toString()+"/workspaces/1").execute();

				String newWorkSpaceString = newWS.getPath().toString();
				
				return newContainer;
		  }
		  
		  private static Variable CopyVariablestoNewContainer(TagManager service, Container newContainer, String newWorkSpaceString, String ExampleWS_String) throws IOException, InterruptedException
		  {
			  Container exampleContainer = new Container();
			  exampleContainer = manager.accounts().containers().get("accounts/38028818/containers/970849").execute();
			  Workspace exampleWS = manager.accounts().containers().workspaces().get("accounts/38028818/containers/970849/workspaces/42/").execute();
			  ExampleWS_String = exampleWS.getPath().toString();
			  Workspace newWS = manager.accounts().containers().workspaces().get(newContainer.getPath().toString()+"/workspaces/1").execute();
			  newWorkSpaceString = newWS.getPath().toString();
			  
			  Variable newVariable = new Variable();
			  List<Variable> exampleVariables = manager.accounts().containers().workspaces().variables().list(ExampleWS_String).execute().getVariable();
							if(exampleVariables != null && !exampleVariables.isEmpty()) {
								for(Variable createdVariable : exampleVariables) {
									Thread.sleep(1000);
								    newVariable = createdVariable.clone();
									newVariable.getWorkspaceId();
									newVariable.setAccountId(null).setContainerId(null).setFingerprint(null).setVariableId(null);
									newVariable = manager.accounts().containers().workspaces().variables().create(newWorkSpaceString, newVariable).execute();
									System.out.println(newVariable.getName() + " created successfuly");
								}
							}
							
							return newVariable;
		  }
		  
		  private static Trigger CopyTriggertoNewContainer(TagManager service, Container newContainer, String newWorkSpaceString, String ExampleWS_String) throws IOException, InterruptedException
		  {
			  Container exampleContainer = new Container();
			  exampleContainer = manager.accounts().containers().get("accounts/38028818/containers/970849").execute();
			  Workspace exampleWS = manager.accounts().containers().workspaces().get("accounts/38028818/containers/970849/workspaces/42/").execute();
			  ExampleWS_String = exampleWS.getPath().toString();
			  Workspace newWS = manager.accounts().containers().workspaces().get(newContainer.getPath().toString()+"/workspaces/1").execute();
			  newWorkSpaceString = newWS.getPath().toString();
			  
			  Trigger copiedTrigger = new Trigger();
			  
			  HashMap<String, String> triggerMap = new HashMap<>();

			List<Trigger> exampleTriggers = manager.accounts().containers().workspaces().triggers().list(ExampleWS_String).execute().getTrigger();
			if(exampleTriggers != null && !exampleTriggers.isEmpty()) {
				for ( Trigger createdTrigger : exampleTriggers) {
					Thread.sleep(1000);
					Trigger newTrigger = createdTrigger.clone();
					newTrigger.setAccountId(null).setContainerId(null).setFingerprint(null).setTriggerId(null).setUniqueTriggerId(null);
					if(createdTrigger.getTriggerId().equals(ALL_PAGES_TRIGGER_ID)) {
						newTrigger.setTriggerId(ALL_PAGES_TRIGGER_ID);
					}
					newTrigger = manager.accounts().containers().workspaces().triggers().create(newWorkSpaceString, newTrigger).execute();
					System.out.println(newTrigger.getName() + " (" + newTrigger.getTriggerId() + ") created");
					triggerMap.put(createdTrigger.getTriggerId(), newTrigger.getTriggerId());
					
					copiedTrigger = createdTrigger;
				}
			}
			
			return copiedTrigger;
		  }
		   
		  private static Tag CopyTagtoNewContainer(TagManager service, Container newContainer, String newWorkSpaceString, String ExampleWS_String, HashMap<String, String> triggerMap) throws IOException, InterruptedException
		  {
			  Container exampleContainer = new Container();
			  exampleContainer = manager.accounts().containers().get("accounts/38028818/containers/970849").execute();
			  Workspace exampleWS = manager.accounts().containers().workspaces().get("accounts/38028818/containers/970849/workspaces/42/").execute();
			  ExampleWS_String = exampleWS.getPath().toString();
			  Workspace newWS = manager.accounts().containers().workspaces().get(newContainer.getPath().toString()+"/workspaces/1").execute();
			  newWorkSpaceString = newWS.getPath().toString();
			  
			  				Tag copiedTag = new Tag();
			  
							List<Tag> exampleTags = manager.accounts().containers().workspaces().tags().list(ExampleWS_String).execute().getTag();
				if(exampleTags != null && !exampleTags.isEmpty()) {
					for (Tag createdTag : exampleTags) {
						Thread.sleep(1000);
						Tag newTag = createdTag.clone();
						newTag.set("parentFolderId",null);
						newTag.set("setupTag",null);
						newTag.set("teardownTag",null);
						newTag.setAccountId(null).setContainerId(null).setFingerprint(null).setTagId(null).setBlockingTriggerId(null).setFiringTriggerId(null);
			
						//Associate new triggers to tag
						List<String> newBlockingTriggers = new ArrayList<String>();
						List<String> newFiringTriggers = new ArrayList<String>();
			
						List<String> exampleBlockingTriggers = createdTag.getBlockingTriggerId();
						if(exampleBlockingTriggers != null && !exampleBlockingTriggers.isEmpty()) {
							for(String blockingTriggerId : exampleBlockingTriggers) {
								if(blockingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) {
									newBlockingTriggers.add(ALL_PAGES_TRIGGER_ID);
								}else {
									newBlockingTriggers.add(triggerMap.get(blockingTriggerId));
								}
			
							}
						}
			
						List<String> exampleFiringTriggers = createdTag.getFiringTriggerId();
						if(exampleFiringTriggers != null && !exampleFiringTriggers.isEmpty()) {
							for(String firingTriggerId : exampleFiringTriggers) {
								if(firingTriggerId.equals(ALL_PAGES_TRIGGER_ID)) {
									newFiringTriggers.add(ALL_PAGES_TRIGGER_ID);
								}else {
									newFiringTriggers.add(triggerMap.get(firingTriggerId));
								}
			
							}
						}
			
						if(!newBlockingTriggers.isEmpty()) { newTag.setBlockingTriggerId(newBlockingTriggers);}
						if(!newFiringTriggers.isEmpty()) { newTag.setFiringTriggerId(newFiringTriggers);}
			
						newTag = manager.accounts().containers().workspaces().tags().create(newWorkSpaceString, newTag).execute();
						System.out.println(newTag.getName() + " created successfuly");
			
						
						copiedTag = newTag;
					}
					
				}
				
				return copiedTag;
		  }
		  
		  
		  


		      /*

		  
		  private static List<Account> getAccountList(TagManager service)
			      throws Exception 
		   {
		        return service.accounts().list().execute().getAccount(); 
		   }
		   
		  private static List<Container> getContainerList(TagManager service)
			      throws Exception 
		   {
		        
			  List<Account> accountList = getAccountList(service);
			  List<Container> containerList = new ArrayList<Container>(); 
			  
			  for(Account a : accountList)
			  {
				  List<Container> currList = service.accounts().containers().list(a.getAccountId()).execute().getContainer();
				  for(Container c : currList)
				  {
					  containerList.add(c);
					  containerList.toArray();
				  }
				  
				  List<Tag> nextList = service.accounts().containers().workspaces().tags().list(a.getAccountId()).execute().getTag();
				  for(Tag tag : nextList ) 
				  {
					  nextList.add(tag);
				  }
				  
			  }
			  
			  
			  return containerList; 	   
		   }
		   
		  private static List<Workspaces> getWorkspaceList(TagManager service)
		  			throws Exception
		  			{
			  			List<Account> Listaccount = getAccountList(service);
			  			List<Container> containerList = new ArrayList<Container>();
			  			List<Workspace> workspaceList = new ArrayList<Workspace>();
			  			
			  			for(Account account: Listaccount)
			  			{
			  				List<Workspace> currentList = service.accounts().containers().workspaces().list(account.getAccountId()).execute().getWorkspace();
			  			}
			  			
			  			return service.accounts().containers().workspaces().list(a.getAccountID()).execute().getWorkspace();
		  			}
		  
		  
		  
		  
		  
		  /*
		   * Find the Example Workspace ID.
		   *
		   * @param service the Tag Manager service object.
		   * @param accountId the ID of the Tag Manager account from which to retrieve the
		   *    Greetings container.
		   *
		   * @return the greetings container if it exists.
		   *
		   *
		  private static Tag findExampleWorkspace(TagManager service, String accountId)
		      throws Exception {
		    for (Tag TestTag :
		        service.accounts().containers().workspaces().tags().list(accountId).execute().getTag()) {
		      if (TestTag.getTagId().equals("4")) {
		        return TestTag;
		      }
		    }
		    throw new IllegalArgumentException("No with that ID in given account");
		  }
	} *
	
	
	
	
	*/
	 /*
	 String[] teststring = {
			  "accountId": "4131139637",
			  "containerId": "9938372",
			  "fingerprint": "1537195749269",
			  "name": "Criteo - Conversion",
			  "parameter": [
			    {
			      "key": "html",
			      "type": "template",
			      "value": "<script type=\"text/javascript\" src=\"//static.criteo.net/js/ld/ld.js\" async=\"true\"></script>\n <script type=\"text/javascript\">\n window.criteo_q = window.criteo_q || [];\n\n window.criteo_q.push(\n { event: \"setAccount\", account: {{Criteo - Account ID}} },\n { event: \"setSiteType\", type: {{deviceType}} },\n { event: \"setEmail\", email: {{hashedEmail cookie}} },\n { event: \"trackTransaction\", deduplication: {{Criteo - isDuplicate}}, id: {{Ecommerce - Order ID}}, item: {{Criteo - Products Format}}}\n);\n</script>\n"
			    },
			    {
			      "key": "supportDocumentWrite",
			      "type": "boolean",
			      "value": "false"
			    }
			  ],
			  "path": "accounts/4131139637/containers/9938372/workspaces/21/tags/1",
			  "tagFiringOption": "oncePerEvent",
			  "tagId": "1",
			  "tagManagerUrl": "https://tagmanager.google.com/#/container/accounts/4131139637/containers/9938372/workspaces/21/tags/1?apiLink=tag",
			  "type": "html",
			  "workspaceId": "21"
			};
	     /*
	      * 
	      * {
"accountId": "4131139637",
"containerId": "9938372",
"fingerprint": "1537195749269",
"name": "Criteo - Conversion",
"parameter": [
{
"key": "html",
"type": "template",
"value": "<script type=\"text/javascript\" src=\"//static.criteo.net/js/ld/ld.js\" async=\"true\"></script>\n <script type=\"text/javascript\">\n window.criteo_q = window.criteo_q || [];\n\n window.criteo_q.push(\n { event: \"setAccount\", account: {{Criteo - Account ID}} },\n { event: \"setSiteType\", type: {{deviceType}} },\n { event: \"setEmail\", email: {{hashedEmail cookie}} },\n { event: \"trackTransaction\", deduplication: {{Criteo - isDuplicate}}, id: {{Ecommerce - Order ID}}, item: {{Criteo - Products Format}}}\n);\n</script>\n"
},
{
"key": "supportDocumentWrite",
"type": "boolean",
"value": "false"
}
],
"path": "accounts/4131139637/containers/9938372/workspaces/21/tags/1",
"tagFiringOption": "oncePerEvent",
"tagId": "1",
"tagManagerUrl": "https://tagmanager.google.com/#/container/accounts/4131139637/containers/9938372/workspaces/21/tags/1?apiLink=tag",
"type": "html",
"workspaceId": "21"
}
	      * 
	      * 
	      * 		  
			private static Variable newVarFromExample(Variable exampleVariable, String VariablePath) {
				Variable var = exampleVariable.clone();
				var = var.setAccountId(null).setContainerId(null).setFingerprint(null).setParentFolderId(null).setTagManagerUrl(null).setWorkspaceId(null).setVariableId(null);
				return var;
			}
		  private static List<Account> getAccountList(TagManager service)
			      throws Exception 
		   {
		    	  return service.accounts().list().execute().getAccount();

		   }
	      * 
	      * 		  
		  private static List<Container> getContainerList(TagManager service, String projectacctID, String ExampleacctID)
			      throws Exception 
		   {
		        
			  List<Account> accountList = getAccountList(service);
			  List<Container> containerList = new ArrayList<Container>(); 
			  
			  for(Account acct : accountList)
			  {
				  List<Container> currList = service.accounts().containers().list(acct.getAccountId()).execute().getContainer();
					  for(Container c : currList)
					  {
						 
					  }
			  }
			  
			  return containerList; 	   
		   }
	      * 
	      */
		  
	}
	