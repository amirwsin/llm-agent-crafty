import tkinter as tk
from tkinter import messagebox


# This function produces a window that allows the user to input comments on llm responses.
def get_user_response():
    # Create a new pop-up window
    popup = tk.Tk()
    popup.wm_title("User Response")

    # Response dictionary to store the user's choice and comment
    response = {'choice': None, 'comment': None}

    # Variable to store user choice
    user_choice = tk.StringVar(value='')

    # Function to handle the submission
    def on_submit():
        # Get the comment from the entry box
        user_comment = comment_entry.get("1.0", tk.END).strip()  # Strip to remove the trailing newline
        # Check if the user made a choice and entered a comment
        if user_choice.get() and user_comment:
            response['choice'] = user_choice.get()
            response['comment'] = user_comment
            print(f"User chose: {response['choice']}")
            print(f"User comments: {response['comment']}")
            popup.destroy()
        else:
            messagebox.showerror("Error", "Please select 'Yes' or 'No' and enter a comment.")

    # User Confirmation Label
    user_confirm_label = tk.Label(popup, text="User confirmed?")
    user_confirm_label.pack()

    # Yes and No Radio Buttons
    yes_radio = tk.Radiobutton(popup, text="Yes", variable=user_choice, value='Yes')
    yes_radio.pack(fill=tk.X, expand=True, padx=50, pady=5)

    no_radio = tk.Radiobutton(popup, text="No", variable=user_choice, value='No')
    no_radio.pack(fill=tk.X, expand=True, padx=50, pady=5)

    # Comment Entry
    tk.Label(popup, text="Your comments:").pack()
    comment_entry = tk.Text(popup, height=4, width=50)  # A larger text box for longer strings
    comment_entry.pack(padx=50, pady=5)

    # Submit button
    submit_button = tk.Button(popup, text="Submit", command=on_submit)
    submit_button.pack(pady=5)

    # This will block the code execution until the window is closed
    popup.mainloop()

    # Return the response
    return response
