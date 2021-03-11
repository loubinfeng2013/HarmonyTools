package com.lbf.lib.inject;

public interface IViewBinder<T> {

    public void bind(T t);

    public void unBind(T t);
}
