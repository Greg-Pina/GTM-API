

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

	public class GTM_API {
		
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
		  
		  
		  public static void main(String[] args) {
			  try {
			      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			      // Authorization flow.
			      Credential credential = authorize();
			      manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
			          .setApplicationName(APPLICATION_NAME).build();
			      
			      TagManager test = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
				          .setApplicationName(APPLICATION_NAME).build();
			      
			      /**   
					 * 
					 * Project info
					 *  
					 **/

				    
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
				     */
			      
			      
			      System.out.println(manager.accounts().containers().list("accounts/4131139637").execute().getContainer());
			      checkContainer("GregPina Test Container", "56800", "GTM-D54D");
			      
			    } catch (Exception e) {
			      e.printStackTrace();
			    }
			  
			
}
	
	  private static void checkContainer(String containerName, String accountID, String ContainerPublicID)
	  {			 
		  try {
			  String accountPath = "accounts/" + accountID;
			  String ExamplePath = "accounts/" + "4131139637" + "/containers/" + "9938372";
			  
			  Container exampleContainer = new Container();
			  
			  //grab Test Container = InfoTrust -> Test Container
			  exampleContainer = manager.accounts().containers().get(ExamplePath).execute();
			  
			  
			  //checking if new container doesn't already exist in target path
			  
			  List<Container> TargetContainer = manager.accounts().containers().list(accountID).execute().getContainer();
			  Container existingContainer = new Container();
			  
			  //checks target path first, if not empty will make a clone
			  if(TargetContainer != null && !TargetContainer.isEmpty())
			  {
				  for(Container accountContainer : TargetContainer)
				  {
					  if(accountContainer.getName().equals(containerName))
					  {
						  existingContainer = accountContainer.clone();
					  }
				  }
			  }
			  
			  if(existingContainer.isEmpty())
			  { 
				  List<Account> AccountList = manager.accounts().list().execute().getAccount();
				  if(AccountList != null && !AccountList.isEmpty())
				  {
					  for(Account account : AccountList)
					  {
						  System.out.println(account.getName());
						  List<Container> accountContainers = manager.accounts().containers().list(account.getAccountId()).execute().getContainer();
						  if(accountContainers != null && !accountContainers.isEmpty())
						  {
							  long foundContainer = accountContainers.stream().filter(container -> container.getPublicId().equals(ContainerPublicID)).count();
							  if (foundContainer > 0)
							  {
								  exampleContainer = accountContainers.stream().filter(container -> container.getPublicId().equals(ContainerPublicID)).collect(Collectors.toList()).get(0);
								  break;
							  }
						  }
					  }
					  Thread.sleep(2000);
				  }
			  }
			  
			  if(!exampleContainer.isEmpty())
			  {
				  CreateContainer(manager, containerName, accountID);
			  }
			  
		  }
			  catch (GoogleJsonResponseException e)
			  {
				  System.err.println("There was a service error:" + e.getDetails().getCode() + ":" + e.getDetails().getMessage());
			  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  catch (InterruptedException e)
			  {
				  e.printStackTrace();
			  }
		  
	  }
		  
		  private static Container CreateContainer(TagManager service,String containerName, String accountID) throws IOException 
		  {
			  Container newContainer = new Container();
			  String accountPath = "accounts/" + accountID;
					  
			  newContainer.setAccountId(accountPath);
			  newContainer.setName(containerName);
			  newContainer.setUsageContext(Arrays.asList("web"));
			  try {
				Container response = manager.accounts().containers().create(accountID, newContainer).execute();
			  }
			  catch (GoogleJsonResponseException e)
			  {
				  System.err.println("There was a service error : " + e.getDetails().getCode() + " : " + e.getDetails().getMessage() );
			  }
			 
			  
			  return newContainer;
		  }

		  private static Variable CreateVariable(TagManager service, String name, String accountID, String containerID, String workspaceID, String ParamType)
		  {
			  Variable newVariable = new Variable();
			  
			  newVariable.setName(name);
			  newVariable.setAccountId(accountID);
			  newVariable.setContainerId(containerID);
			  newVariable.setWorkspaceId(workspaceID);
			  
			  
			  return newVariable;
		  }
		  
		  private static Tag CreateTag(TagManager service, String name, String accountID, String containerID, String workspaceID, String ParamType)
		  {
			  Tag newTag = new Tag();
			  
			  newTag.setName(name);
			  newTag.setAccountId(accountID);
			  newTag.setContainerId(containerID);
			  newTag.setWorkspaceId(workspaceID);
			  newTag.setType(ParamType);
			  
			  
			  Parameter param0 = new Parameter();
			  param0.setType(null);
			  param0.setKey(null);
			  param0.setValue(null);
			  
			  Parameter param1 = new Parameter();
			  param1.setType(null);
			  param1.setKey(null);
			  param1.setValue(null);
			  
			  Parameter param2 = new Parameter();
			 
			  
			  
			  
			  
			  return newTag;
		  }
		  
		  private static Trigger CreateTrigger(TagManager service, String name, String accountID, String containerID, String workspaceID, String ParamType)
		  {
			  Trigger newTrigger = new Trigger();
			  
			  newTrigger.setName(name);
			  newTrigger.setAccountId(accountID);
			  newTrigger.setContainerId(containerID);
			  newTrigger.setWorkspaceId(workspaceID);
			  
			  
			  return newTrigger;
		  }

		  

		  

			private static Credential authorize() throws Exception {
			    // Load client secrets.
			    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
			        new InputStreamReader(GTM_API.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

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
	