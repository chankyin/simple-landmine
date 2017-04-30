package io.github.chankyin.simplelandmine.mine;

import android.view.View;

public enum TileState{
    EMPTY{
        @Override
        public View getView(){
            return null;
        }
    },
    OPENED{
        @Override
        public View getView(){
            return null;
        }
    },
    FLAGGED{
        @Override
        public View getView(){
            return null;
        }
    },
    PROBLEMATIC_WRONG_FLAG{
        @Override
        public View getView(){
            return null;
        }
    },
    EXPLODED_MINE{
        @Override
        public View getView(){
            return null;
        }
    },
    UNOPENED_MINE{
        @Override
        public View getView(){
            return null;
        }
    };

    public abstract View getView();
}
