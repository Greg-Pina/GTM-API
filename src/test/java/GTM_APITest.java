

	/**
	 * Access and manage a Google Tag Manager account.
	 */

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.*;
import java.util.List;
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
import com.google.api.services.tagmanager.TagManagerScopes;
import com.google.api.services.tagmanager.model.Account;
import com.google.api.services.tagmanager.model.Condition;
import com.google.api.services.tagmanager.model.Trigger;
import com.google.api.services.tagmanager.model.Container;
import com.google.api.services.tagmanager.model.ListAccountsResponse;
import com.google.api.services.tagmanager.model.ListContainerVersionsResponse;
import com.google.api.services.tagmanager.model.ListContainersResponse;
import com.google.api.services.tagmanager.model.ListTagsResponse;
import com.google.api.services.tagmanager.model.ListTriggersResponse;
import com.google.api.services.tagmanager.model.ListVariablesResponse;
import com.google.api.services.tagmanager.model.Parameter;
import com.google.api.services.tagmanager.model.Tag;
import com.google.api.services.tagmanager.model.UserPermission;
import com.google.api.services.tagmanager.model.Variable;
import com.google.api.services.tagmanager.model.Workspace;
import com.google.gson.JsonObject;

	public class GTM_APITest {
		
		  // Path to client_secrets.json file downloaded from the Developer's Console.
		  // The path is relative to GTM_APITest.java.
		  private static final String CLIENT_SECRET_JSON_RESOURCE = "client_secrets.json";

		  // The directory where the user's credentials will be stored for the application.
		  private static final File DATA_STORE_DIR = new File("/Users/Greg/eclipse-workspace/GregPina-APITestRepo/src/main/java/");

		  private static final String APPLICATION_NAME = "GTM_APITest";
		  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
		  private static NetHttpTransport httpTransport;
		  private static FileDataStoreFactory dataStoreFactory;
		  
		  //project
		  public static List<Account>   ProjectAccount;
		  public static List<Container> ProjectContainer;
		  public static List<Workspace> ProjectWorkSpace;
		  public static List<Tag>       ProjectTag;
		  public static List<Trigger>   ProjectTrigger;
		  public static List<Variable>  ProjectVariable;
		  
		  
		  //examples
		  public static List<Account>   ExampleAccount;
		  public static List<Container> ExampleContainer;
		  public static List<Workspace> ExampleWorkSpace;
		  public static List<Tag>       ExampleTag;
		  public static List<Trigger>   ExampleTrigger;
		  public static List<Variable>  ExampleVariable;
		  
		  
		  public static void main(String[] args) {
			  try {
			      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			      // Authorization flow.
			      Credential credential = authorize();
			      TagManager manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
			          .setApplicationName(APPLICATION_NAME).build();
			      
			      /**   
					 * 
					 * Project info
					 *  
					 **/
					// Get tag manager account ID for Project.
				      String ProjectAccountID = "3982950028";
				      
				      // Get GTM Container ID for Project
				      String ProjectContainerID = "9814638";
				      
				      // Get GTM Workspace ID for Project
				      String ProjectWorkspaceID = "7";
				      
				      String ProjectPath = "accounts/" + ProjectAccountID + "/containers/" + ProjectContainerID + "/workspaces/" + ProjectWorkspaceID;

				      // Get GTM account ID for example 
				      String ExampleAccountId = "4131139637";
				      String OnboardingAccountId = "4131139637";
				      
				      // Get GTM Container ID for example
				      String ExampleContainerId = "9938372";
				      String OnboardingContainerId = "9938372";
				      
				      // Get GTM Workspace ID for example
				      String ExampleWorkspaceID = "21";
				     
				     String ExamplePath = "accounts/" + ExampleAccountId + "/containers/" + ExampleContainerId + "/workspaces/" + ExampleWorkspaceID;
				      
				      // Info from example account
				     ListContainersResponse EXContainer = manager.accounts().containers().list("accounts/" + ExampleAccountId).execute();
				     ListTagsResponse  EXTagList = manager.accounts().containers().workspaces().tags().list(ExamplePath).execute();
				     ListVariablesResponse EXVariableList = manager.accounts().containers().workspaces().variables().list(ExamplePath).execute();
				     ListTriggersResponse EXTriggerList = manager.accounts().containers().workspaces().triggers().list(ExamplePath).execute();
				    
				    // System.out.println(EXTagList.getTag());
				     
				     List<Tag> ProjectTags = new ArrayList<Tag>();
				     Tag t1, t2, t3, t4,t5,t6;
				     t1 = EXTagList.getTag().get(0);
				     ProjectTags.add(t1);
				     
				     Get container = manager.accounts().containers().get("/accounts/56800/containers/98189/");
				     
				     
				    //manager.accounts().containers().create(parent, content)..execute();

				    
		
				     
				    // System.out.println(t1.get("fingerprint"));

				 
				    // System.out .print
				    
				    /*
				     * 
				     * what I need to do is create a function that will allow me to be able to create a project container
				     * 
				     * then function to call the example container
				     * 
				     * 		after calling example container, new function will create new tags for my project container from the example
				     * 		same with triggers
				     * 		same with variables
				     * 
				     * 		ProtectedQuery function 
				     * 
				     * 
				     * 
				     * 
				     * 
				     */
				 
				 System.out.println(t1.getParameter());
				     
				 System.out.println(EXContainer.getContainer());
				 
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
				      */

				   			
				     				     
				     // Info from project account
				     ListContainersResponse ProjectContainer = manager.accounts().containers().list("accounts/" + ProjectAccountID).execute();
				     ListTagsResponse  ProjectTagList = manager.accounts().containers().workspaces().tags().list(ProjectPath).execute();
				     
				    
				     
				    
				    
				    /*
				     * 
				     for( Tag tag : ProjectTagList.getTag())
					    {
					    		
					    		 tag.setAccountId(ProjectAccountID);
						    	 tag.setContainerId(ProjectContainerID);
						    	
						    	 manager.accounts().containers().workspaces().tags().create("accounts/" + ProjectAccountID + "/containers/" + ProjectContainerID + "/workspaces/" + ProjectWorkspaceID, tag).execute();
	    					
    					}
				     
			     	
				     
				     for( Trigger trigger : ProjectTriggerList.getTrigger())
				     {

				    		 trigger.setAccountId(ProjectAccountID);
					    	 
					    	 trigger.setContainerId(ProjectContainerID);
					    	 
					    	 manager.accounts().containers().workspaces().triggers().create("accounts/" + ProjectAccountID + "/containers/" + ProjectContainerID + "/workspaces/" + ProjectWorkspaceID, trigger).execute();
					    	
				   
				     }
				     
				     
				     
				     for( Variable variable : ProjectVariableList.getVariable())
				     {

				    		 variable.setAccountId(ProjectAccountID);
				    		 
						     variable.setContainerId(ProjectContainerID);
						     
						     manager.accounts().containers().workspaces().variables().create("accounts/" + ProjectAccountID + "/containers/" + ProjectContainerID + "/workspaces/" + ProjectWorkspaceID, variable).execute();
			    		 
				    	 
				     }
				    
				     */
				    
				     
				     
				     
				     
				      /**
				      List<Account> Project = getAccountList(manager, ProjectAccountID);
				      List<Container> ProjectContainer = getContainerList(manager, ProjectAccountID);
				      List<Workspace> ProjectWorkspace = getWorkspaceList(manager, ProjectContainerID, ProjectAccountID);
				      **/
				      
			      
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			  
			  
			  
		  
}			  

		  private static JsonObject TagJSON(String acctID, String contID, String Param, String Priority, String Type)
		  {
			  JsonObject obj = new JsonObject();
			  
			  
			  
			  
			  return obj;
		  }
		  
		  /*
		  public static Variable getCreateUpdateVariable(TagManager manager, Variable exampleVariable, Workspace wks)
					throws IOException, InterruptedException {
				boolean createVar = ExampleVariable == null
						|| !ExampleVariable.stream().anyMatch(v -> v.getName().equals(exampleVariable.getName()));

				Variable variable = !createVar
						? ExampleVariable.stream().filter(v -> v.getName().equals(exampleVariable.getName())).findFirst()
								.get()
						: manager.accounts().containers().workspaces().variables().create(wks.getPath(), variable);
						if (createVar) {
							Thread.sleep(rateLimitTimeSec);
							logVariable(Operation.CREATE, wks, variable, Level.INFO, "create new variable");
						} else if (hasVariableChanged(variable, exampleVariable)) {
							// UPDATE VARIABLE
							variable.setParameter(exampleVariable.getParameter());
							variable.setNotes(exampleVariable.getNotes());
							variable.setType(exampleVariable.getType());
							variable = protectedQuery(manager.accounts().containers().workspaces().variables().update(variable.getPath(), variable)
									);
							logVariable(Operation.UPDATE, wks, variable, Level.INFO, "UPDATED Variable");
						}
						return variable;
					}
		  
		  */
			private static Variable newVarFromExample(Variable exampleVariable) {
				Variable var = exampleVariable.clone();
				var = var.setAccountId(null).setContainerId(null).setFingerprint(null).setParentFolderId(null).setTagManagerUrl(null).setWorkspaceId(null).setVariableId(null);
				return var;
			}
		  private static List<Account> getAccountList(TagManager service)
			      throws Exception 
		   {
		    	  return service.accounts().list().execute().getAccount();

		   }
		  
		  private static List<Container> grabContainer(TagManager service)
		  {
			return ExampleContainer;
			  
		  }
		  
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
		  
		  private static Container CreateContainer(TagManager service, Container name) 
		  {
			  name.setName("test_container");
			  name.setUsageContext(Arrays.asList("web", "android", "ios"));
			 
			  
			  return name;
		  }
		  

			private static Credential authorize() throws Exception {
			    // Load client secrets.
			    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
			        new InputStreamReader(GTM_APITest.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

			    // Set up authorization code flow for all auth scopes.
			    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
			        JSON_FACTORY, clientSecrets, TagManagerScopes.all()).setDataStoreFactory(dataStoreFactory)
			        .build();

			    // Authorize.
			    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("GA Support");
			  }
		      
		      
		      //This pulls the tags from example container
		      
			      
	}
		      
		      /*
		       * Return example container (as object)
		       * Return all tags from example container as arraylist
		       * Return all triggers from example container as arraylist
		       * Return all variables from example container as arraylist
		       * 
		       * For Each var in VarExample
		       * 	Create variable in Greg_Pina test Container
		       * 
		       *    used object as return as param
		       *    
		       *    Then use .ToList method (or w/e) 
		       *    
		       *    
		       *    
		       *
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		  }
		  
		  
		private static List<UserPermission> getUP(TagManager service, String accountId)
			throws Exception
		{
			List<UserPermission> UPList = new ArrayList<UserPermission>();
			
			return UPList;
		}
		  
		private static Credential authorize() throws Exception {
		    // Load client secrets.
		    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
		        new InputStreamReader(GTM_APITest.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));
		    // Set up authorization code flow for all auth scopes.
		    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
		        JSON_FACTORY, clientSecrets, TagManagerScopes.all()).setDataStoreFactory(dataStoreFactory)
		        .build();
		    // Authorize.
		    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		  }
		  /*
		   * Find the Greg_Pina_Test container ID.
		   *
		   * @param service the Tag Manager service object.
		   * @param accountId the ID of the Tag Manager account from which to retrieve the
		   *    Greetings container.
		   *
		   * @return the Greg_Pina_Test container if it exists.
		   *
		   *
		
		  private static Container findProjectContainer(TagManager service, String ProjectcontainerId)
		      throws Exception {
		    for (Container container :
		        service.accounts().containers().list(ProjectcontainerId).execute().getContainer()) {
		      if (container.getName().equals("Greg_Pina_Test_Container")) {
		        return container;
		      }
		    }
		    throw new IllegalArgumentException("No container named Greg_Pina_Test_Container in given account");
		  }
		  
		  /*
		   * Find the Example container ID.
		   *
		   * @param service the Tag Manager service object.
		   * @param accountId the ID of the Tag Manager account from which to retrieve the
		   *    Greetings container.
		   *
		   * @return the greetings container if it exists.
		   *
		   *
		   *
		   *
		  private static Container findTestContainer(TagManager service, String ExampleContainerId)
		      throws Exception {
		    for (Container container :
		        service.accounts().containers().list(ExampleContainerId).execute().getContainer()) {
		      if (container.getContainerId().equals("98189")) {
		        return container;
		      }
		    }
		    throw new IllegalArgumentException("No container with that ID in given account");
		  }
		  
		  private static List<Account> getAccountList(TagManager service)
			      throws Exception 
		   {
		        return service.accounts().list().execute().getAccount(); 
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
		  
		  private static List<Tag> getTagList(TagManager service)
		  		throws Exception
		  		{
			  		return service.accounts().containers().workspaces().tags().list("92").execute().getTag();
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
