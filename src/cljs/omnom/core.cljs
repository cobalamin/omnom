(ns omnom.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [goog.dom :as gdom]
            [simple-om-draggable.core :refer [draggable-item]]))

(def app-state
  (atom
   {:notes [{:position {:x 100
                        :y 50}
             :w 200
             :content "# Notes!\nNice *nice* __nice__\n```js\nfunction add(a, b) {\n  return a + b;\n}\n```"}
            {:position {:x 200
                        :y 300}
             :w 400
             :content "[membero conde!](https://github.com/clojure/core.logic/wiki/A-Core.logic-Primer)"}
            {:position {:x 500
                        :y 20}
             :w 200
             :content "# Head\n## lines\n### are *cool*"}]}))

(defn req-anim-frame [fn]
  (js/requestAnimationFrame fn))

(defn get-by-tag-name
  ([tag-name] (get-by-tag-name tag-name nil))
  ([tag-name node] (array-seq (gdom/getElementsByTagNameAndClass tag-name nil node))))

(defn highlight! [node]
  (req-anim-frame
   #(let [pre-blocks (get-by-tag-name "pre" node)]
     (doall
      (map (fn [pre-block]
             (.highlightBlock js/hljs pre-block))
           pre-blocks)))))

(defcomponent note-view [note owner]
  (init-state [_]
   {:mounted false
    :dragging false})

  (did-mount [_]
   (om/set-state! owner :mounted true))

  (render [_]
   (let [{:keys [w h content]
          {:keys [x y]} :position} note]
     (when (om/get-state owner :mounted)
       (highlight! (om/get-node owner)))
     (dom/div {:class "note"
               :style {:width w}
               :dangerouslySetInnerHTML {:__html (js/marked content)}}))))

(def draggable-note-view
  (draggable-item note-view [:position]))

(defcomponent root-view [app owner]
  (render [_]
   (dom/div
    (om/build-all draggable-note-view (:notes app)))))

(defn main []
  (om/root
    root-view
    app-state
    {:target (gdom/getElement "app")
     :tx-listen
       (fn [{:keys [old-value new-value]}]
         (.log js/console (str old-value " -> " new-value)))}))

