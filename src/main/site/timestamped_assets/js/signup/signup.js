const form = document.getElementById("signupForm");
form.addEventListener("submit", function (event) {
  event.preventDefault();
  const formData = new FormData(form);
  console.log(Object.fromEntries(formData));
});
