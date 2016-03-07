#!/bin/bash
set -x
set -e

command_exists () {
    type "$1" &> /dev/null ;
}

# nice prompt
cat /vagrant/profile >> ~/.profile

# OCaml opam
if command_exists opam ; then
  opam init -y &>/dev/null
  eval `opam config env`
  echo '' >> $HOME/.profile
  echo '#OPAM' >> $HOME/.profile
  echo 'eval `opam config env`' >> $HOME/.profile

  # Oasis
  opam install oasis &>/dev/null
fi
