export function showModal(title, content, callback) {
  const modalContainer = document.getElementById("custom-modal");
  const modalTitle = document.getElementById("modal-title");
  const modalContent = document.getElementById("modal-content");
  modalTitle.textContent = title;
  modalContent.textContent = content;
  modalContainer.classList.remove("hidden");

  const modalOkButton = document.getElementById("modal-ok-button");
  if (modalOkButton) {
    modalOkButton.addEventListener("click", () => {
      closeModal(callback);
    });
  }
}

export function closeModal(callback) {
  const modalContainer = document.getElementById("custom-modal");
  modalContainer.classList.add("hidden");
  if (callback) {
    callback();
  }
}

document.addEventListener("DOMContentLoaded", () => {
  window.closeModal = closeModal;
});
