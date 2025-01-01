FROM maven:3.8.5-openjdk-17
WORKDIR /workspace

# Install zsh and dependencies using microdnf
RUN microdnf install -y \
    zsh \
    curl \
    git \
    && microdnf clean all

# Install Oh My Zsh
RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

# Install zsh-autosuggestions and zsh-syntax-highlighting plugins
RUN git clone https://github.com/zsh-users/zsh-autosuggestions /root/.oh-my-zsh/custom/plugins/zsh-autosuggestions \
    && git clone https://github.com/zsh-users/zsh-syntax-highlighting.git /root/.oh-my-zsh/custom/plugins/zsh-syntax-highlighting
