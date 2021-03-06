import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
* This class controls the GUI which allows a user to write a review.
*/
public class WriteAReviewController {

	private String restaurant_name;
	private int number_of_stars = 0;
	private String restaurant_review;
	private MelpMember reviewer;
	private CreateMelpDatabase db = new CreateMelpDatabase();

	@FXML
	private ResourceBundle resources;

	@FXML
	private Label headerLabel;

	@FXML
	private MenuButton star_list;

	@FXML
	private URL location;

	@FXML
	private TextField restaurant;

	@FXML
	private TextArea review;

	/**
	* Adds a review to the database
	* @param the current restaurant review
	*/
	public void addReviewToDatabase(RestaurantReview curr_rev) {
		db.addReview(reviewer, curr_rev);
	}

	/**
	* Checks if a restaurant is in the database
	* @throws SQLException
	* @return true if the restaurant is successfully added to database
	*/
	public boolean restaurantInDatabase() throws SQLException {
		return db.searchRestaurants(restaurant_name);
	}

	/**
	* Sends a user back to the user profile
	* @param the event of the user
	* @throws IOException
	*/
	@FXML
	void returnToProfile(ActionEvent event) throws IOException {
		Stage next_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
		next_stage.setTitle("My Profile");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("UserProfileUI.fxml"));
		UserProfileController controller = new UserProfileController();
		controller.setMember(reviewer);
		loader.setController(controller);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		next_stage.setScene(scene);
	}

	/**
	 * This is the "Submit review" button. It makes sure the review isn't vulgar, then moves to the ViewReview page
	 * @param event - this is the button press action
	 * @throws SQLException 
	 * @throws Exception - in case the gui being created doesn't exist (which is impossible)
	 */
	@FXML
	void submitRestaurantReview(ActionEvent event) throws IOException, SQLException {
		restaurant_name = restaurant.getText();
		restaurant_review = review.getText();
		RestaurantReview new_review = new RestaurantReview(reviewer.getName(), restaurant_review, number_of_stars, restaurant_name);
		if (number_of_stars != 0) {
			if (new_review.approveRequest()) {
				if (new_review.isNotSpam(reviewer, restaurant_name)) {
					if (restaurantInDatabase()) {
						reviewer.addReviewToMyReviews(new_review);
						reviewer.addRestaurantToMyRestaurants(new_review.getRestaurantUnderReview());
						addReviewToDatabase(new_review);
						Stage next_stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
						next_stage.setTitle("View Review");
						FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewReviewUI.fxml"));
						ViewReviewController controller = new ViewReviewController();
						controller.setReview(new_review);
						controller.setMember(reviewer);
						loader.setController(controller);
						Parent root = loader.load();
						Scene scene = new Scene(root);
						next_stage.setScene(scene);
					}
					else {
						headerLabel.setText("This restaurant is not in our system. You can't write a review for it yet.");
						headerLabel.setStyle("-fx-text-fill: red");
					}
				}
				else {
					headerLabel.setText("You have already reviewed this restaurant. No spam allowed!");
					headerLabel.setStyle("-fx-text-fill: red");
				}
			} 
			else {
				headerLabel.setText("Your review was vulgar. Try again");
				headerLabel.setStyle("-fx-text-fill: red");
				reviewer.incrementVulgarPosts();
				if(reviewer.maxVulgarPosts()) {
					reviewer.blockUser();
					showStage();
				}
			}
		}
		else {
			headerLabel.setText("You must give the restaurant a number of stars (1-5)");
			headerLabel.setStyle("-fx-text-fill: red");
		}
	}

	/**
	 * Displays the stage for blocking a member
	 * @throws IOException 
	 */
	public void showStage() throws IOException{
		try {
			BlockedMessageGUI block = new BlockedMessageGUI();
			Stage primaryStage = new Stage();
			block.start(primaryStage);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gives a restaurant a review of one star
	 * @param the event of the user
	 */
	@FXML
	void OneStarReview(ActionEvent event) {
		number_of_stars = 1;
		star_list.setText("1 Star");
	}

	/**
	 * Gives a restaurant a review of two stars
	 * @param the event of the user
	 */
	@FXML
	void TwoStarReview(ActionEvent event) {
		number_of_stars = 2;
		star_list.setText("2 Stars");
	}

	/**
	 * Gives a restaurant a review of three stars
	 * @param the event of the user
	 */
	@FXML
	void ThreeStarReview(ActionEvent event) {
		number_of_stars = 3;
		star_list.setText("3 Stars");
	}

	/**
	 * Gives a restaurant a review of four stars
	 * @param the event of the user
	 */
	@FXML
	void FourStarReview(ActionEvent event) {
		number_of_stars = 4;
		star_list.setText("4 Stars");
	}

	/**
	 * Gives a restaurant a review of five stars
	 * @param the event of the user
	 */
	@FXML
	void FiveStarReview(ActionEvent event) {
		number_of_stars = 5;
		star_list.setText("5 Stars");
	}

	/**
	 * Initializes the number of stars for a user to choose from
	 */
	@FXML
	void initialize() {
		headerLabel.setText("Melp: Review a Restaurant");
		star_list.setText("Number of Stars");
	}

	/**
	 * Sets a member to the current member
	 * @param the current member
	 */
	public void setMember(MelpMember current_member) {
		reviewer = current_member;
	}

}