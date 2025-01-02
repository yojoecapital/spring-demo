FROM maven:3.8.5-openjdk-17

ARG USER_ID
ARG GROUP_ID
ARG USER
RUN groupadd -g ${GROUP_ID} "${USER}" && \
    useradd -m -u ${USER_ID} -g ${GROUP_ID} "${USER}"
    
# Install zsh and dependencies using microdnf
RUN microdnf install -y \
    zsh \
    curl \
    git \
    && microdnf clean all
    
USER "${USER}"

# Install Oh My Zsh
RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

# Install zsh-autosuggestions and zsh-syntax-highlighting plugins
RUN git clone https://github.com/zsh-users/zsh-autosuggestions "/home/$USER/.oh-my-zsh/custom/plugins/zsh-autosuggestions" \
    && git clone https://github.com/zsh-users/zsh-syntax-highlighting.git "/home/$USER/.oh-my-zsh/custom/plugins/zsh-syntax-highlighting"

WORKDIR "/home/${USER}"
