# Oh My Zsh!
export ZSH="$HOME/.oh-my-zsh"
ZSH_THEME="custom"

# Stops the weird '%' from being printed
unsetopt PROMPT_SP 

# Plugins
plugins=(
    git
    zsh-autosuggestions
    zsh-syntax-highlighting
)
source $ZSH/oh-my-zsh.sh

# Aliases
alias cls="clear"
alias ni="touch"

# Kill word with ctrl+backspace
bindkey '^H' backward-kill-word

#THIS MUST BE AT THE END OF THE FILE FOR SDKMAN TO WORK!!!
export SDKMAN_DIR="/opt/sdkman"
[[ -s "/opt/sdkman/bin/sdkman-init.sh" ]] && source "/opt/sdkman/bin/sdkman-init.sh"
