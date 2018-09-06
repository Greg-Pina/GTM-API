

	/**
	 * Access and manage a Google Tag Manager account.
	 */

	import java.io.File;
	import java.io.InputStreamReader;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.List;

	import org.apache.commons.codec.language.bm.Rule;

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
	import com.google.api.services.tagmanager.TagManager.Accounts;
	import com.google.api.services.tagmanager.TagManager.Accounts.Containers.Get;
	import com.google.api.services.tagmanager.TagManagerScopes;
	import com.google.api.services.tagmanager.model.Account;
	import com.google.api.services.tagmanager.model.Condition;
	import com.google.api.services.tagmanager.model.Container;
	import com.google.api.services.tagmanager.model.Parameter;
	import com.google.api.services.tagmanager.model.Tag;
	import com.google.api.services.tagmanager.model.Workspace;
	public class GTM_APITest {
		
		  // Path to client_secrets.json file downloaded from the Developer's Console.
		  // The path is relative to GTM_APITest.java.
		  private static final String CLIENT_SECRET_JSON_RESOURCE = "client_secrets.json";

		  // The directory where the user's credentials will be stored for the application.
		  private static final File DATA_STORE_DIR = new File("/Users/Greg/eclipse-workspace/Google_Tag_Manager/src/main/java/client_secrets.json\n" + 
		  		"\n" + 
		  		"");

		  private static final String Google_Tag_Manager = "GTM_APITest";
		  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
		  private static NetHttpTransport httpTransport;
		  private static FileDataStoreFactory dataStoreFactory;

		  public static void main(String[] args) {
		    try {
		    	

		      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

		      // Authorization flow.
		      Credential credential = authorize();
		      TagManager manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
		          .setApplicationName(Google_Tag_Manager).build();

		      // Get tag manager account ID for Project.
		      String accountId = "3982950028";

		      // Find the Greg_Pina_Test Project container.
		      Container Greg_Pina_Test = findProjectContainer(manager, accountId);
		      String ProjectcontainerId = Greg_Pina_Test.getContainerId();
		      
		      // Get GTM account ID for example container
		      String ExampleAccountId = "56800";
		      
		      // Find the example container
		      Container Example_Container = findTestContainer(manager, ExampleAccountId);
		      String ExampleContainerId = Example_Container.getContainerId();
		      Workspace Example_Workspace = 
		      
		      //This pulls the tags from example container

			      
		      
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
		       */

		    } catch (Exception e) {
		      e.printStackTrace();
		    }
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
		   */
		
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
		   */
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
		  
		  /*
		   * Find the Example Workspace ID.
		   *
		   * @param service the Tag Manager service object.
		   * @param accountId the ID of the Tag Manager account from which to retrieve the
		   *    Greetings container.
		   *
		   * @return the greetings container if it exists.
		   *
		   */
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
	}
