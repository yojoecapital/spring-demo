# Oh My Zsh!
export ZSH="$HOME/.oh-my-zsh"
ZSH_THEME="robbyrussell"

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
