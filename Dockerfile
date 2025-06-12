FROM debian:12-slim

ARG USER_ID
ARG GROUP_ID
ARG USER

# Create user
RUN groupadd -g ${GROUP_ID} "${USER}" && \
    useradd -m -u ${USER_ID} -g ${GROUP_ID} "${USER}"
    
# Install zsh and dependencies using microdnf
RUN apt-get update && apt-get install -y \
  	sudo \
    nano \
    java-common \
    zsh \
    curl \
    wget \
    zip \
    unzip \
    tar \
    gnupg \
    software-properties-common \
    git \
    build-essential \
    net-tools procps \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Give the user sudo privileges
RUN usermod -aG sudo "${USER}"
RUN echo "${USER} ALL=(ALL) NOPASSWD:ALL" > "/etc/sudoers.d/${USER}"
    
# Install Maven
RUN wget -O /tmp/apache-maven.tar.gz https://dlcdn.apache.org/maven/maven-3/3.9.10/binaries/apache-maven-3.9.10-bin.tar.gz && \
    tar -xzf /tmp/apache-maven.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.9.10/bin/mvn /usr/bin/mvn && \
    rm /tmp/apache-maven.tar.gz

# Install SDKMAN 
ENV SDKMAN_DIR="/opt/sdkman"
RUN curl -s "https://get.sdkman.io" | bash
SHELL ["/bin/bash", "-c"]    
RUN bash -c "source /opt/sdkman/bin/sdkman-init.sh && \
    sdk install java 11.0.24-amzn && \
    sdk install java 8.0.422-amzn && \
    sdk install java 17.0.12-amzn && \
    sdk default java 8.0.422-amzn"
    
USER "${USER}"

# Install Oh My Zsh
RUN sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"

# Add the hostname+robbyrussell theme
COPY "zsh/custom.zsh-theme" "/home/$USER/.oh-my-zsh/custom/themes/"

# Install zsh-autosuggestions and zsh-syntax-highlighting plugins
RUN git clone https://github.com/zsh-users/zsh-autosuggestions "/home/$USER/.oh-my-zsh/custom/plugins/zsh-autosuggestions" \
    && git clone https://github.com/zsh-users/zsh-syntax-highlighting.git "/home/$USER/.oh-my-zsh/custom/plugins/zsh-syntax-highlighting"


# Make these directories
RUN mkdir -p "/home/${USER}/.vscode-server"
RUN mkdir -p "/home/${USER}/.m2"

WORKDIR "/home/${USER}"
